package com.berry.manulrpc.provider;

import com.berry.manulrpc.api.ICalculator;
import com.berry.manulrpc.provider.service.CalculatorImpl;
import com.berry.manulrpc.rpc.RpcInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/11/27
 * fileName：ServiceBootstrap
 * Use：ALL 框架做的事，将服务注册，开始监听请求，根据请求调用的方法 在 注册中心找到对应的服务 -> invoke
 */
public class ServiceBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(ServiceBootstrap.class);

    private static final ConcurrentHashMap<String, Class<?>> serviceRegistry = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        register(ICalculator.class, CalculatorImpl.class);
        start();
    }

    public static void register(Class<?> serviceInterface, Class<?> impl) {
        serviceRegistry.put(serviceInterface.getName(), impl);
    }

    private static void start() {
        while (true) {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try (ServerSocket serverSocket = new ServerSocket(6666)) {
                Socket accept = serverSocket.accept();
                inputStream = new ObjectInputStream(accept.getInputStream());
                Object readObject = inputStream.readObject();
                RpcInvocation rpcInvocation;
                if (readObject instanceof RpcInvocation) {
                    rpcInvocation = (RpcInvocation) readObject;
                } else {
                    logger.error("params  not support");
                    return;
                }
                String serviceName = rpcInvocation.getServiceName();
                String methodName = rpcInvocation.getMethodName();
                Class<?>[] parameterTypes = rpcInvocation.getParameterTypes();
                Object[] arguments = rpcInvocation.getArguments();

                Class<?> serviceClass = serviceRegistry.get(serviceName);
                if (serviceClass == null) {
                    throw new ClassNotFoundException(serviceName + "not found!");
                }

                Method method = serviceClass.getMethod(methodName, parameterTypes);
                Object result = method.invoke(serviceClass.newInstance(), arguments);
                outputStream = new ObjectOutputStream(accept.getOutputStream());
                outputStream.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
