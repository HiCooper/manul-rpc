package com.berry.manulrpc.consumer;

import com.berry.manulrpc.api.ICalculator;
import com.berry.manulrpc.rpc.Invoker;
import com.berry.manulrpc.rpc.ProxyFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/11/27
 * fileName：Test
 * Use：
 */
public class Test {

    private static final ICalculator calculator;

    // 框架需要做的事
    static {
        ProxyFactory factory = new ProxyFactory();
        Invoker<ICalculator> invoker = new Invoker<>(ICalculator.class);
        calculator = factory.getProxy(invoker, new Class<?>[]{ICalculator.class});
    }

    public static void main(String[] args) {
        int add = calculator.add(1, 2);
        System.out.println(add);
    }
}
