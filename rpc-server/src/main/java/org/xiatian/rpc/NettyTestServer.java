package org.xiatian.rpc;

import org.xiatian.rpc.common.annotation.ServiceScan;
import org.xiatian.rpc.common.serializer.CommonSerializer;
import org.xiatian.rpc.transport.RpcServer;
import org.xiatian.rpc.transport.netty.server.NettyServer;

/**
 * 测试用Netty服务提供者（服务端）
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 9999);
        server.start();
    }
}
