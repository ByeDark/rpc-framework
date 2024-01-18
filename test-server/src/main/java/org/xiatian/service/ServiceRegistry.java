package org.xiatian.service;

/**
 * 服务注册接口
 */
public interface ServiceRegistry {

    <T> void register(T service) throws Exception;
    Object getService(String serviceName) throws Exception;
}
