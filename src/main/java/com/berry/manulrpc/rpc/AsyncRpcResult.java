package com.berry.manulrpc.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author HiCooper.
 * @version 1.0
 * @date 2020/12/4
 * fileName：AsyncRpcResult
 * Use：
 */
public class AsyncRpcResult implements Result {

    private static final Logger logger = LoggerFactory.getLogger(AsyncRpcResult.class);

    private Invocation invocation;

    private CompletableFuture<AppResponse> responseFuture;

    public AsyncRpcResult(CompletableFuture<AppResponse> future, Invocation invocation) {
        this.responseFuture = future;
        this.invocation = invocation;
    }

    @Override
    public Object recreate() throws Throwable {
        return getAppResponse().recreate();
    }

    public Result getAppResponse() {
        try {
            if (responseFuture.isDone()) {
                return responseFuture.get();
            }
        } catch (Exception e) {
            // This should not happen in normal request process;
            logger.error("Got exception when trying to fetch the underlying result from AsyncRpcResult.");
            throw new RpcException(e);
        }
        return new AppResponse();
    }

    public static AsyncRpcResult newDefaultAsyncResult(Object value, Throwable throwable, Invocation invocation) {
        CompletableFuture<AppResponse> future = new CompletableFuture<>();
        AppResponse result = new AppResponse();
        result.setResult(value);
        result.setException(throwable);
        future.complete(result);
        return new AsyncRpcResult(future, invocation);
    }
}
