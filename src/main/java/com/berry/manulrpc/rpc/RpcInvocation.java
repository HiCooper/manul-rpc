package com.berry.manulrpc.rpc;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
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

    public RpcInvocation(Method method, String serviceName, Object[] arguments) {
        this(method, serviceName, method.getParameterTypes(), arguments);
    }

    public RpcInvocation(Method method, String serviceName, Class<?>[] parameterTypes, Object[] arguments) {
        this.methodName = method.getName();
        this.serviceName = serviceName;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments == null ? new Object[0] : arguments;
    }
}
