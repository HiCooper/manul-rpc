package com.berry.manulrpc.rpc.remoting.client;

import com.alibaba.fastjson.JSON;
import com.berry.manulrpc.rpc.AppResponse;
import com.berry.manulrpc.rpc.remoting.NettyCodecAdapter;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import static com.berry.manulrpc.rpc.remoting.NettyEventLoopFactory.eventLoopGroup;
import static com.berry.manulrpc.rpc.remoting.NettyEventLoopFactory.socketChannelClass;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

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

    private static final String DEFAULT_SOCKS_PROXY_PORT = "1080";

    private Bootstrap bootstrap;

    /**
     * 当前连接关闭状态控制
     */
    private volatile boolean closed;

    /**
     * netty client bootstrap
     */
    private static final EventLoopGroup NIO_EVENT_LOOP_GROUP = eventLoopGroup(DEFAULT_IO_THREADS, "NettyClientWorker");

    /**
     * current channel. Each successful invocation of {@link NettyClient#doConnect()} will
     * replace this with new channel and close old channel.
     * <b>volatile, please copy reference to use.</b>
     */
    private volatile Channel channel;

    @Override
    protected void doOpen() {
        final NettyClientHandler nettyClientHandler = new NettyClientHandler();
        bootstrap = new Bootstrap();
        bootstrap.group(NIO_EVENT_LOOP_GROUP)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .channel(socketChannelClass());
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                NettyCodecAdapter adapter = new NettyCodecAdapter();
                ch.pipeline()
                        .addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                        .addLast("decoder", new StringDecoder())
                        .addLast("encoder", new StringEncoder())
                        .addLast("client-idle-handler", new IdleStateHandler(10 * 1000, 0, 0, MILLISECONDS))
                        .addLast("handler", nettyClientHandler);
//                String socksProxyHost = "localhost";
//                int socksProxyPort = Integer.parseInt(DEFAULT_SOCKS_PROXY_PORT);
//                Socks5ProxyHandler socks5ProxyHandler = new Socks5ProxyHandler(new InetSocketAddress(socksProxyHost, socksProxyPort));
//                ch.pipeline().addFirst(socks5ProxyHandler);
            }
        });
    }

    @Override
    protected void doConnect() {
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(8888));
        try {
            boolean ret = future.awaitUninterruptibly(3000, MILLISECONDS);
            if (ret && future.isSuccess()) {
                Channel newChannel = future.channel();
                try {
                    // Close old channel
                    // copy reference
                    Channel oldChannel = this.channel;
                    if (oldChannel != null) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close old netty channel " + oldChannel + " on create new netty channel " + newChannel);
                            }
                            oldChannel.close();
                        } catch (Exception e) {
                            logger.error("error: {}", e.getMessage());
                        }
                    }
                } finally {
                    if (this.isClosed()) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close new netty channel " + newChannel + ", because the client closed.");
                            }
                            newChannel.close();
                        } finally {
                            this.channel = null;
                        }
                    } else {
                        this.channel = newChannel;
                    }
                }
            } else if (future.cause() != null) {
                throw new RuntimeException(future.cause().getMessage());
            } else {
                throw new RuntimeException("client-side timeout..");
            }

        } finally {
            // ignore temp
        }
    }

    public CompletableFuture<Object> send(Object msg) {
        channel.writeAndFlush(JSON.toJSONString(msg) + "\n");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // todo get response ，与 NettyClientHandler -》 channelRead 联通
        // mock
        return CompletableFuture.supplyAsync(() -> {
            AppResponse response = new AppResponse();
            response.setResult(2);
            return response;
        });
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    protected void doClose() {

    }

    @Override
    protected void doDisConnect() {

    }
}
