package org.xiatian.rpc.transport;

import org.xiatian.rpc.common.serializer.CommonSerializer;
import org.xiatian.rpc.entity.RpcRequest;

import java.util.Properties;

/**
 * 客户端类通用接口
 */
public interface RpcClient {

    //序列化方式
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    //发送请求
    Object sendRequest(RpcRequest rpcRequest);

}
