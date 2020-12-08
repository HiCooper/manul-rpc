package com.berry.manulrpc.rpc.remoting.client;

import com.berry.manulrpc.rpc.AppResponse;
import com.berry.manulrpc.rpc.remoting.DefaultFuture;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/11/30
 * fileName：NettyClientHandler
 * Use：
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel active: {}", ctx.channel().id());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel inactive: {}", ctx.channel().id());
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("channel: {} read: {}", ctx.channel().id(), msg);
        // ignore return msg type check
        Integer result ;
        try {
            result = Integer.parseInt(msg.toString());
        } catch (Exception e) {
            return;
        }
        DefaultFuture.received(ctx.channel(), new AppResponse(result));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        logger.info("channel: {}, write: {}", ctx.channel().id(), msg);

        promise.addListener(future -> {
            if (future.isSuccess()) {
                // if our future is success, mark the future to sent.
//                handler.sent(channel, msg);
                return;
            }

            Throwable t = future.cause();
//            if (t != null && isRequest) {
//                Request request = (Request) msg;
//                Response response = buildErrorResponse(request, t);
//                handler.received(channel, response);
//            }
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("{}, userEventTriggered", ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("{}, exceptionCaught, cause： ", ctx.channel().id(), cause);
    }
}
