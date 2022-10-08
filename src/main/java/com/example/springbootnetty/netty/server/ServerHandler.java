package com.example.springbootnetty.netty.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.tiles3.SpringWildcardServletTilesApplicationContext;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ServerHandler
 * @Description netty 服务端handler
 * @Auther tanyi
 * @Date 2022/9/23
 * @Version 1.0
 **/
@Slf4j
@Component
// 指示可以将带注释的ChannelHandler的同一实例多次添加到一个或多个ChannelPipelines，而无需争用条件。
// 如果未指定此注释，则每次将其添加到管道时都必须创建一个新的处理程序实例，因为它具有非共享状态，例如成员变量
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当有客户端发送数据到服务端时触发
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // ByteBuf buf = (ByteBuf) msg;
        // log.info("接收到客户端消息");
        // log.info("client request:" + buf.toString(CharsetUtil.UTF_8));
        //
        // SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // String result = format.format(new Date()) + "\n";
        // ctx.write(Unpooled.copiedBuffer(result.getBytes()));

        // log.info("收到客户端消息：{}", msg);
        // JSONObject requestInfo = JSON.parseObject(msg.toString());
        // String msgType = requestInfo.getString("msgType");
        //
        // switch (msgType) {
        //     // 回复客户端请求
        //     case "req":
        //         doReply(ctx);
        //         break;
        //     default:
        //         break;
        // }

        log.info("服务器收到消息: {}", msg.toString());
        ctx.writeAndFlush(Unpooled.copiedBuffer("你也好哦，客户端 ，" + msg, CharsetUtil.UTF_8));


    }



    /**
     * 连接断开时触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开连接:{}", getClientIp(ctx.channel()));
        
    }

    /**
     * 当通道就绪就会触发该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("通道已连接");
        String clientIp = getClientIp(ctx.channel());
        NettyClient nettyClient = new NettyClient((SocketChannel) ctx.channel(), clientIp);

        NettyChannelMap.add(clientIp,nettyClient);

    }



    /**
     * 当消息读取完成时触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将发送缓冲区的消息全部写到SocketChannel中
        // ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 类名：ServerHandler.java
     * 类说明: 当有新的客户端连接时，用于保存客户端信息
     * 创建者: tanyi
     * 创建时间: 2022/9/25 22:53
     * 版本号: 1.0.0
    */
    public static class NettyChannelMap {
        // 所有连接中的客户端列表
        public static Map<String, NettyClient> clientMap = new ConcurrentHashMap<>();

        /**
         * 新增连接
         * @param clientId
         * @param client
         */
        public static void add(String clientId, NettyClient client) {
            clientMap.put(clientId, client);
        }

        /**
         * 根据id获取NettyClient对象
         * @param clientId
         * @return
         */
        public static NettyClient get(String clientId) {
            return clientMap.get(clientId);
        }

        /**
         * 移除客户端连接
         * @param socketChannel
         */
        public static void remove(SocketChannel socketChannel) {
            for (Map.Entry entry : clientMap.entrySet()) {
                if (((NettyClient) entry.getValue()).getChannel() == socketChannel) {
                    clientMap.remove(entry.getKey());
                }
            }
        }

    }

    /**
     * @description: 当收到客户端的消息后，进行处理
     * @param ctx
     * @Author: wuyong
     * @Date: 2019/08/30 14:10:59
     * @return: void
     */
    private void doReply(ChannelHandlerContext ctx) {
        String reply = "{\"msgType\":\"reply\",\"data\":\"回复的数据\"}";
        ctx.channel().writeAndFlush(reply);
    }

    /**
     * 类名：ServerHandler.java
     * 类说明: 封装客户端的信息
     * 创建者: tanyi
     * 创建时间: 2022/9/25 22:54
     * 版本号: 1.0.0
    */
    @Data
    public static class NettyClient {
        // 客户端与服务端的连接channel
        private SocketChannel channel;
        // 客户端ip地址
        private String clientIp;

        public NettyClient(SocketChannel channel, String clientIp) {
            this.channel = channel;
            this.clientIp = clientIp;
        }
    }
    
    

    /**
     * 获取channel对象的ip
     * @param channel
     * @return
     */
    private String getClientIp(Channel channel) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();

        return inetSocketAddress.getHostName();
    }
}
