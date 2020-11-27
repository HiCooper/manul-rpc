package com.berry.manulrpc.rpc;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/11/27
 * fileName：RpcInvocation
 * Use：
 */
@Data
public class RpcInvocation implements Serializable {

    private String serviceName;

    private String methodName;

    private Object[] arguments;

    private Class<?>[] parameterTypes;

    private Class<?> returnType;

    private Type[] returnTypes;
}
