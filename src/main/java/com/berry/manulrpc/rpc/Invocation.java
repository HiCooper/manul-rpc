package com.berry.manulrpc.rpc;

import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA.
 *
 * @author HiCooper.
 * @version 1.0
 * @date 2020/12/4
 * fileName：Invocation
 * Use：
 */
public interface Invocation {


    /**
     * get the interface name
     *
     * @return
     */
    String getServiceName();

    /**
     * get method name.
     *
     * @return method name.
     * @serial
     */
    String getMethodName();

    /**
     * get arg
     *
     * @return
     */
    Object[] getArguments();


    /**
     * get parameter types.
     *
     * @return parameter types.
     * @serial
     */
    Class<?>[] getParameterTypes();

    /**
     * get return type
     *
     * @return
     */
    Class<?> getReturnType();

    /**
     * get return types
     *
     * @return
     */
    Type[] getReturnTypes();

}
