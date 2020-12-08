package com.berry.manulrpc.rpc.remoting;

import com.berry.manulrpc.rpc.AppResponse;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author HiCooper.
 * @version 1.0
 * @date 2020/12/8
 * fileName：DefaultFuture
 * Use：
 */
public class DefaultFuture extends CompletableFuture<Object> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultFuture.class);

    private static final Map<Long, Channel> CHANNELS = new ConcurrentHashMap<>();

    private static final Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    private DefaultFuture(Channel channel) {
        // todo id 临时设置，需要包装一个 Request ，每个request 包含一个唯一ID
        long id = 111L;
        // put into waiting map.
        FUTURES.put(id, this);
        CHANNELS.put(id, channel);
    }

    public static DefaultFuture newFuture(Channel channel) {
        return new DefaultFuture(channel);
    }

    public void cancel() {
        this.cancel(true);
    }

    public static void received(Channel channel, AppResponse response) {
        try {
            // requestId temp set
            DefaultFuture future = FUTURES.remove(111L);
            if (future != null) {
                future.doReceived(response);
            }
        }finally {
            CHANNELS.remove(111L);
        }
    }

    private void doReceived(AppResponse res) {
        if (res == null) {
            throw new IllegalStateException("response cannot be null");
        }
        this.complete(res);
    }
}
