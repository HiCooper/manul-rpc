package com.berry.manulrpc.rpc;

import com.berry.manulrpc.rpc.proxy.InvokerInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/11/27
 * fileName：ProxyFactory
 * Use：
 */
public class ProxyFactory {

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
    }
}
