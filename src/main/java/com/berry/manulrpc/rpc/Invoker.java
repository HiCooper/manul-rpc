package com.berry.manulrpc.rpc;

import lombok.Data;

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
}
