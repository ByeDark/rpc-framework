package org.xiatian.rpc.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiatian.rpc.entity.RpcRequest;
import org.xiatian.rpc.entity.RpcResponse;
import org.xiatian.rpc.transport.netty.client.NettyClient;
import org.xiatian.rpc.util.RpcMessageChecker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * RPC客户端动态代理
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        //构建发送的消息
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .heartBeat(false)
                .build();
        RpcResponse rpcResponse = null;
        if (client instanceof NettyClient) {
            try {
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
                rpcResponse = completableFuture.get();
            } catch (Exception e) {
                logger.error("方法调用请求发送失败", e);
                return null;
            }
        }
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        assert rpcResponse != null;
        return rpcResponse.getData();
    }
}
