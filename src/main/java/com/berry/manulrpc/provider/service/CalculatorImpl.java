package com.berry.manulrpc.provider.service;

import com.berry.manulrpc.api.ICalculator;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/11/27
 * fileName：CalculatorImpl
 * Use：
 */
public class CalculatorImpl implements ICalculator {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
