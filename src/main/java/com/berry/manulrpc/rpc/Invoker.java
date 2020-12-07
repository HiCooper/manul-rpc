package com.berry.manulrpc.rpc;

import com.berry.manulrpc.rpc.remoting.client.NettyClient;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/11/27
 * fileName：Invoker
 * Use：调用发起者
 */
@Data
public class Invoker<T> {

    private Class<T> type;

    public Invoker(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("service type == null");
        }
        this.type = type;
    }

    public Result invoke(Invocation invocation) throws RpcException {
        AsyncRpcResult asyncRpcResult;
        try {
            asyncRpcResult = doInvoke(invocation);
        } catch (Exception e) {
            asyncRpcResult = AsyncRpcResult.newDefaultAsyncResult(null, e, invocation);
        }
        return asyncRpcResult;
    }

    private AsyncRpcResult doInvoke(final Invocation invocation) {
        NettyClient client = new NettyClient();
        RpcInvocation rpcInvocation = (RpcInvocation) invocation;
        CompletableFuture<AppResponse>  future = client.send(rpcInvocation).thenApply(obj -> (AppResponse) obj);
        return new AsyncRpcResult(future, invocation);
    }
}
