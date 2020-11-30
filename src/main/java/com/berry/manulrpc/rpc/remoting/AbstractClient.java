package com.berry.manulrpc.rpc.remoting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/11/30
 * fileName：AbstractClient
 * Use：
 */
public abstract class AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    private final Lock connectLock = new ReentrantLock();

    private volatile boolean closed;

    public AbstractClient() {
        try {
            doOpen();
        } catch (Throwable t) {
            close();
        }
        try {
            connect();
        } catch (Exception e) {
            close();
        }
    }

    private void connect() {
        connectLock.lock();
        try {
            doConnect();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            connectLock.unlock();
        }
    }

    private void disconnect() {
        connectLock.lock();
        try {
            try {
                doDisConnect();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            connectLock.unlock();
        }
    }

    private void close() {
        connectLock.lock();
        try {
            //set state

            // disconnect
            try {
                disconnect();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }

            try {
                doClose();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            connectLock.unlock();
        }
    }

    protected abstract void doOpen();

    protected abstract void doClose();

    protected abstract void doConnect();

    protected abstract void doDisConnect();
}
