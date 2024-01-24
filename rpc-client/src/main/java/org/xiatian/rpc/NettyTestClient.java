package org.xiatian.rpc;

import org.xiatian.rpc.remote.ByeService;
import org.xiatian.rpc.remote.HelloObject;
import org.xiatian.rpc.remote.HelloService;
import org.xiatian.rpc.transport.RpcClient;
import org.xiatian.rpc.transport.RpcClientProxy;
import org.xiatian.rpc.transport.netty.client.NettyClient;
import org.xiatian.rpc.common.serializer.CommonSerializer;

/**
 * 测试用Netty消费者
 */
public class NettyTestClient {

    public static void main(String[] args) {
        //初始化配置，设置选择哪一种序列化方式
        RpcClient client = new NettyClient(CommonSerializer.JSON_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        //通过这种序列化方式对服务进行动态代理
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        //------------------发送数据一--------------------------------------------
        //创建发送数据
        HelloObject object = new HelloObject(1, "hello ~");
        //通过代理类发送数据，并打印得到结果
        System.out.println(helloService.hello(object));
        //------------------发送数据二--------------------------------------------
        //与上面一样的流程再发一个
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        //代理类调用然后得到结果
        System.out.println(byeService.bye("bye bye ~"));
    }
}
