package org.xiatian.rpc.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiatian.rpc.factory.ThreadPoolFactory;
import org.xiatian.rpc.util.NacosUtil;


public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        //在JVM摧毁前获得一个进程，就是钩子进程
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
