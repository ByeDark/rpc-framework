package org.xiatian.util;

import org.xiatian.dto.RpcRequest;
import org.xiatian.dto.RpcResponse;
import org.xiatian.service.NettyClient;
import org.xiatian.service.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * JDK动态代理实现远程过程调用的消息包装
 */
public class RpcClientProxy implements InvocationHandler {
    private final String host;
    private final int port;

    public RpcClientProxy(NettyClient nettyClient) {
        this.host = nettyClient.getHost();
        this.port = nettyClient.getPort();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //这里通过jdk动态代理，将rpc构建出来的message直接转换成
        //这里利用建造者模式来构建对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcClient rpcClient = new NettyClient(host,port);
        return ((RpcResponse<?>) rpcClient.sendRequest(rpcRequest)).getData();
    }
}