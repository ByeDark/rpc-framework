package org.xiatian.service;

import org.xiatian.dto.RpcRequest;
import org.xiatian.dto.RpcResponse;
import org.xiatian.enumeration.ResponseCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class RequestHandler {

    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());

    public Object handle(RpcRequest rpcRequest, Object service) {
        Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("服务:"+rpcRequest.getInterfaceName()+"成功调用方法:"+rpcRequest.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.warning("调用或发送时有错误发生："+e);
        } return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws IllegalAccessException, InvocationTargetException {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail();
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
