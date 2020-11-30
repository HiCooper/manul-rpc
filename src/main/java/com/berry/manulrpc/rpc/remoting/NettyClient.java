package com.berry.manulrpc.rpc.remoting;

import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.berry.manulrpc.rpc.remoting.NettyEventLoopFactory.eventLoopGroup;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/11/30
 * fileName：NettyClient
 * Use：
 */
public class NettyClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    /**
     * netty client bootstrap
     */
    private static final EventLoopGroup NIO_EVENT_LOOP_GROUP = eventLoopGroup(DEFAULT_IO_THREADS, "NettyClientWorker");


    @Override
    protected void doOpen() {

    }

    @Override
    protected void doClose() {

    }

    @Override
    protected void doConnect() {

    }

    @Override
    protected void doDisConnect() {

    }
}
