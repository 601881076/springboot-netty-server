package com.example.springbootnetty.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ClientHandler
 * @Description 客户端handler
 * @Auther tanyi
 * @Date 2022/9/23
 * @Version 1.0
 **/
@Slf4j
@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 连接到服务器时触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("服务器已连接");
        // for (int i = 0; i < 10; i++) {
        //     ctx.writeAndFlush(Unpooled.copiedBuffer("current time\n", CharsetUtil.UTF_8));
        // }

        ctx.writeAndFlush(Unpooled.copiedBuffer("current time\n", CharsetUtil.UTF_8));

    }

    /**
     * 发生异常时触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 消息到来时触发
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        log.info("服务端返回消息 {}", buf.toString(CharsetUtil.UTF_8));

        log.info("current Time{}", buf.toString(CharsetUtil.UTF_8));

    }
}
