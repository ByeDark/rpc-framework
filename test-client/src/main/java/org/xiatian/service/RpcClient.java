package org.xiatian.service;

import org.xiatian.dto.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}