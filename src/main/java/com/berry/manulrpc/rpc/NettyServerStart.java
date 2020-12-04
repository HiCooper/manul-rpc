package com.berry.manulrpc.rpc;

import com.berry.manulrpc.api.ICalculator;
import com.berry.manulrpc.provider.service.CalculatorImpl;
import com.berry.manulrpc.rpc.remoting.server.NettyServer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author HiCooper.
 * @version 1.0
 * @date 2020/12/4
 * fileName：ServerStart
 * Use：
 */
public class NettyServerStart {

    public static void main(String[] args) {
        new NettyServer();
    }

}
