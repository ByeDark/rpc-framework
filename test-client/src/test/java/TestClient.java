import org.xiatian.api.HelloObject;
import org.xiatian.api.HelloService;
import org.xiatian.service.NettyClient;
import org.xiatian.service.RpcClient;
import org.xiatian.util.RpcClientProxy;


public class TestClient {
    public static void main(String[] args) {
        //创建NettyClient实例
        NettyClient client = new NettyClient("127.0.0.1", 9999);
        //对NettyClient进行包装，包装后自动将client封装成
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        //得到代理类,包装,并没有执行里面的函数
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        //得到发送消息
        HelloObject object = new HelloObject(12, "This is a message");
        //得到返回结果，执行了代理类
        String res = helloService.hello(object);
        System.out.println(res);
    }
}