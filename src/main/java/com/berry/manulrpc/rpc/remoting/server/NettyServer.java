package com.berry.manulrpc.rpc.remoting.server;

import com.berry.manulrpc.rpc.remoting.NettyCodecAdapter;
import com.berry.manulrpc.rpc.remoting.NettyEventLoopFactory;
import com.berry.manulrpc.rpc.remoting.client.NettyClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/12/1
 * fileName：NettyServer
 * Use：
 */
public class NettyServer extends AbstractServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    /**
     * netty server bootstrap.
     */
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    /**
     * the cache for alive worker channel.
     * <ip:port, dubbo channel>
     */
    private Map<String, Channel> channels;

    /**
     * the boss channel that receive connections and dispatch these to worker channel.
     */
    private Channel channel;

    private static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    public NettyServer() {
        super();
    }

    @Override
    protected void doOpen() throws Throwable {
        bootstrap = new ServerBootstrap();

        // 负责客户端接收的线程池
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");
        // 处理IO的工作线程池
        workerGroup = NettyEventLoopFactory.eventLoopGroup(DEFAULT_IO_THREADS, "NettyServerWorker");

        final NettyServerHandler nettyServerHandler = new NettyServerHandler();
        channels = nettyServerHandler.getChannels();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NettyEventLoopFactory.serverSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        NettyCodecAdapter adapter = new NettyCodecAdapter();
                        ch.pipeline()
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, 10 * 1000, MILLISECONDS))
                                .addLast("handler", nettyServerHandler);
                    }
                });
        // bind
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(8888));
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();
    }

    @Override
    protected void doClose() throws Throwable {
        // 关闭 channel
        try {
            if (channel != null) {
                // unbind.
                channel.close();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        // 关闭 线程池
        try {
            if (bootstrap != null) {
                bossGroup.shutdownGracefully().syncUninterruptibly();
                workerGroup.shutdownGracefully().syncUninterruptibly();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * just for main test
     */
    public void holdOn() {
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
