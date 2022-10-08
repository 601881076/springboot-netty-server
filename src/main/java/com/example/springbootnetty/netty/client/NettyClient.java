package com.example.springbootnetty.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName NettyClient
 * @Description netty客户端
 * @Auther tanyi
 * @Date 2022/9/23
 * @Version 1.0
 **/
@Slf4j
public class NettyClient {

    // 创建线程组
    private static final EventLoopGroup group = new NioEventLoopGroup();

    // 全局通道连接
    private static SocketChannel socketChannel = null;

    public static void main(String[] args) {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 使用netty自带的粘包处理器，注意顺序需要自定义业务前
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            socketChannel.pipeline().addLast(new StringEncoder());
                            socketChannel.pipeline().addLast(new StringDecoder());

                            // 自定义处理程序
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });

            // 绑定端口并同步等待
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8081).sync();

            if (channelFuture.isSuccess()) {
                socketChannel = (SocketChannel) channelFuture.channel();
                log.info("connect server success");
            }

            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static SocketChannel getSocketChannel() {
        return socketChannel;
    }
}
