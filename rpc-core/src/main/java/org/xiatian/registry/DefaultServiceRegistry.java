package org.xiatian.registry;

import org.xiatian.enumeration.RpcError;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class DefaultServiceRegistry implements ServiceRegistry {

    private static final Logger logger = Logger.getLogger(DefaultServiceRegistry.class.getName());

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized <T> void register(T service) throws Exception {
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0) {
            throw new Exception("接口错误");
        }
        for(Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("向接口:" + Arrays.toString(interfaces) +"注册服务:"+serviceName);
    }

    @Override
    public synchronized Object getService(String serviceName) throws Exception {
        Object service = serviceMap.get(serviceName);
        if(service == null) {
            throw new Exception("不存在服务");
        }
        return service;
    }
}
