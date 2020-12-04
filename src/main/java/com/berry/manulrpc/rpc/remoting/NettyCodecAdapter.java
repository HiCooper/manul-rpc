package com.berry.manulrpc.rpc.remoting;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/12/1
 * fileName：NettyCodecAdapter
 * Use：
 */
public final class NettyCodecAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyCodecAdapter.class);

    private final ChannelHandler encoder = new InternalEncoder();

    private final ChannelHandler decoder = new InternalDecoder();

    public ChannelHandler getEncoder() {
        return encoder;
    }

    public ChannelHandler getDecoder() {
        return decoder;
    }

    private static class InternalEncoder extends MessageToMessageEncoder<Object> {
        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
            logger.info("********************* 封包 *********************");
        }
    }

    private static class InternalDecoder extends MessageToMessageDecoder<Object> {
        @Override
        protected void decode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
            logger.info("********************* 拆包 *********************");
        }
    }
}
