package org.xiatian.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiatian.rpc.common.enumeration.RpcError;
import org.xiatian.rpc.common.exception.RpcException;
import org.xiatian.rpc.common.serializer.CommonSerializer;
import org.xiatian.rpc.entity.RpcRequest;
import org.xiatian.rpc.entity.RpcResponse;
import org.xiatian.rpc.factory.SingletonFactory;
import org.xiatian.rpc.common.loadbalancer.LoadBalancer;
import org.xiatian.rpc.common.loadbalancer.RandomLoadBalancer;
import org.xiatian.rpc.registry.NacosServiceDiscovery;
import org.xiatian.rpc.registry.ServiceDiscovery;
import org.xiatian.rpc.transport.RpcClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * NIO方式消费侧客户端类
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;
    private final UnprocessedRequests unprocessedRequests;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    public NettyClient() {
        this(new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        Properties prop = new Properties();
        try (InputStream input = NettyClient.class.getClassLoader().getResourceAsStream("application.yaml")) {
            if (input == null) {
                logger.info("未检测到序列化配置，使用了默认的JSON序列化器");
            }
            // 加载配置文件
            prop.load(input);
            // 读取配置信息
            String propertySerializer = prop.getProperty("serializer");
            System.out.println("从配置文件读取到序列化方式："+propertySerializer);
            switch (propertySerializer) {
                case "json" -> serializer = CommonSerializer.getByCode(CommonSerializer.JSON_SERIALIZER);
                case "kyro" -> serializer = CommonSerializer.getByCode(CommonSerializer.KRYO_SERIALIZER);
                case "hessian" -> serializer = CommonSerializer.getByCode(CommonSerializer.HESSIAN_SERIALIZER);
                default -> serializer = CommonSerializer.getByCode(DEFAULT_SERIALIZER);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}
