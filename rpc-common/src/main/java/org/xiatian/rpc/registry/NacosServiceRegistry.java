package org.xiatian.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiatian.rpc.common.enumeration.RpcError;
import org.xiatian.rpc.common.exception.RpcException;
import org.xiatian.rpc.util.NacosUtil;

import java.net.InetSocketAddress;

/**
 * Nacos 服务注册中心
 * 初始化   服务的注册
 * 调用的全部封装到Util里面
 */
public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

}
