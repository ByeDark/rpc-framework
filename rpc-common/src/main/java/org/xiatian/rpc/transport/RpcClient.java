package org.xiatian.rpc.transport;

import org.xiatian.rpc.common.serializer.CommonSerializer;
import org.xiatian.rpc.entity.RpcRequest;

/**
 * 客户端类通用接口
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}
