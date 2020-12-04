package com.berry.manulrpc.rpc.remoting.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/12/1
 * fileName：AbstractServer
 * Use：
 */
public abstract class AbstractServer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);

    private int accepts;

    private int idleTimeout;

    AbstractServer() {
        try {
            doOpen();
            if (logger.isInfoEnabled()) {
                logger.info("Start bind ...");
            }
        } catch (Throwable t) {
            throw new RuntimeException("Failed to bind ...");
        }
    }

    protected abstract void doOpen() throws Throwable;

    protected abstract void doClose() throws Throwable;

}
