package com.example.springbootnetty.netty.server;

import ch.qos.logback.core.pattern.color.BoldWhiteCompositeConverter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import jdk.nashorn.internal.objects.NativeUint8Array;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName NettyServer
 * @Description 创建NettyServer类添加@Component注解交由容器管理
 * @Auther tanyi
 * @Date 2022/9/23
 * @Version 1.0
 **/
@Component
@Slf4j
public class NettyServer {

    // 服务端NIO线程组
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workGroup = new NioEventLoopGroup();

    @Autowired
    private ServerHandler serverHandler;

    /**
     * netty启动方法
     *
     * @param host
     * @param port
     * @return
     */
    public ChannelFuture start(String host, int port) {
        ChannelFuture channelFuture = null;

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    // 使消息立即发出去，不用等待到一定的数据量才发出去
                    // .option(ChannelOption.TCP_NODELAY, true)
                    //设置队列大小
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 保持长连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            // netty自带的粘包处理
                            // LineBasedFrameDecoder能够将接收到的数据在行尾进行拆分。
                            // 设置解码帧的最大长度，如果帧的长度超过此值抛出异常 io.netty.handler.codec.TooLongFrameException: frame length (12) exceeds the allowed maximum (10)
                            // pipeline.addLast(new LineBasedFrameDecoder(1024));

                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                            // 自定义服务处理
                            pipeline.addLast(serverHandler);



                        }
                    });

            // 绑定端口并同步等待
            channelFuture = serverBootstrap.bind(host, port).sync();
            log.info("================>>>>>> netty1 start up success!!!");

            // 同步监听关闭事件
            channelFuture.channel().closeFuture().sync();
            log.info("================>>>>>> netty2 start up success!!!");

        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            // 关闭netty线程组
            close();
        }

        return channelFuture;
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        log.info("================>>>>>> shutdown Netty server success!!!");
    }

}
