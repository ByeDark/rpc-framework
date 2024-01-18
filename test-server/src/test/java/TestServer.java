import org.xiatian.api.HelloService;
import org.xiatian.api.HelloServiceImpl;
import org.xiatian.service.DefaultServiceRegistry;
import org.xiatian.service.NettyServer;
import org.xiatian.service.RpcServer;
import org.xiatian.service.ServiceRegistry;


public class TestServer {

    public static void main(String[] args) throws Exception {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }
}