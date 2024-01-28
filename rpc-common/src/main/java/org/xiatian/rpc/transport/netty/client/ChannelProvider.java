package org.xiatian.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiatian.rpc.common.codec.CommonDecoder;
import org.xiatian.rpc.common.codec.CommonEncoder;
import org.xiatian.rpc.common.serializer.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 用于获取 Channel 对象
 */
public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static final Bootstrap bootstrap = initializeBootstrap();
    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException {
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if(channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                /*自定义序列化编解码器*/
                // RpcResponse -> ByteBuf
                //增加心跳机制
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel = null;
        try {
            channel = connect(inetSocketAddress);
        } catch (ExecutionException e) {
            logger.error("连接客户端时有错误发生", e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    private static Channel connect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        ChannelProvider.bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端连接成功!");
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    private static Bootstrap initializeBootstrap() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
