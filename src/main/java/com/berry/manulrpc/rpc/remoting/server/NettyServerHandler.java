package com.berry.manulrpc.rpc.remoting.server;

import com.alibaba.fastjson.JSON;
import com.berry.manulrpc.api.ICalculator;
import com.berry.manulrpc.provider.service.CalculatorImpl;
import com.berry.manulrpc.rpc.RpcInvocation;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/12/1
 * fileName：NettyServerHandler
 * Use：
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    /**
     * 模拟服务注册中心
     */
    private static ConcurrentHashMap<String, Class<?>> serviceRegistry = new ConcurrentHashMap<>();

    public NettyServerHandler() {
        register(ICalculator.class, CalculatorImpl.class);
    }

    /**
     * 注册服务
     * @param serviceInterface
     * @param impl
     */
    public static void register(Class<?> serviceInterface, Class<?> impl) {
        serviceRegistry.put(serviceInterface.getName(), impl);
    }

    /**
     * the cache for alive worker channel.
     * <ip:port, dubbo channel>
     */
    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel income = ctx.channel();
        logger.info("channel active: {}", income.id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel inactive: {}", ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("channel: {} read: {}", ctx.channel().id(), msg);

        RpcInvocation rpcInvocation =  JSON.parseObject((String) msg, RpcInvocation.class);

        Channel channel = ctx.channel();

        String serviceName = rpcInvocation.getServiceName();
        String methodName = rpcInvocation.getMethodName();
        Class<?>[] parameterTypes = rpcInvocation.getParameterTypes();
        Object[] arguments = rpcInvocation.getArguments();

        Class<?> serviceClass = serviceRegistry.get(serviceName);
        if (serviceClass == null) {
            throw new ClassNotFoundException(serviceName + "not found!");
        }

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        // todo serviceClass.newInstance() 可以缓存起来
        Object result = method.invoke(serviceClass.newInstance(), arguments);

        // todo 返回类型的处理，如果是void方法调用，这里不需要再写result消息了
        String res = result.toString() + "\n";
        channel.writeAndFlush(res);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        logger.info("channel: {}, write: {}", ctx.channel().id(), msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("{}, userEventTriggered", ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("{}, exceptionCaught, cause：", ctx.channel().id(), cause);
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }
}
