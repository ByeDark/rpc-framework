package org.xiatian.rpc.transport.netty.client;

import org.xiatian.rpc.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理完毕的消息都会放在这个单例的容器之中，处理完了就会从hashmap中移除
 */
public class UnprocessedRequests {

    private static final ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures
            = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        unprocessedResponseFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }

    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }

}
