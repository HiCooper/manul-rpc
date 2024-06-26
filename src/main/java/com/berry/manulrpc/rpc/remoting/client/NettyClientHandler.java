package com.berry.manulrpc.rpc.remoting.client;

import com.alibaba.fastjson.JSON;
import com.berry.manulrpc.rpc.AppResponse;
import com.berry.manulrpc.rpc.RpcInvocation;
import com.berry.manulrpc.rpc.remoting.DefaultFuture;
import com.berry.manulrpc.rpc.util.DataConverter;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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


    Map<String, Class<?>> channelIdWithReturnTypeMap = new HashMap<>();

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
        Class<?> returnType = channelIdWithReturnTypeMap.get(ctx.channel().id().toString());
        // 返回数据类型转换
        Object resultObj = DataConverter.parseResultToTargetReturnType(msg, returnType);
        DefaultFuture.received(ctx.channel(), new AppResponse(resultObj));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        logger.info("channel: {}, write: {}", ctx.channel().id(), msg);
        // 记录channelId 和 返回类型的关系，收到返回数据时进行类型处理转化
        RpcInvocation rpcInvocation =  JSON.parseObject((String) msg, RpcInvocation.class);
        channelIdWithReturnTypeMap.put(ctx.channel().id().toString(), rpcInvocation.getReturnType());

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
        // todo 心跳检测这里做
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("{}, exceptionCaught, cause： ", ctx.channel().id(), cause);
    }
}
