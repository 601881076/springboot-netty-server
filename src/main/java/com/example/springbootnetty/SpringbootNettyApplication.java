package com.example.springbootnetty;

import com.example.springbootnetty.netty.server.NettyServer;
import io.netty.channel.ChannelFuture;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.annotation.Resource;

@SpringBootApplication
@Slf4j
public class SpringbootNettyApplication implements CommandLineRunner {

    @Resource
    private NettyServer nettyServer;

    // @Override
    // protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    //     return builder.sources(SpringbootNettyApplication.class);
    // }

    public static void main(String[] args) {
        SpringApplication.run(SpringbootNettyApplication.class, args);

        // 开启netty服务
        // NettyServer nettyServer = new NettyServer();
        //
        // nettyServer.start("localhost", 8081);
        //
        // log.info("======服务已经启动========");
    }


    /**
     * 在SpringBoot启动类上实现CommandLineRunner接口。
     * 注入容器中的NettyServer对象，在run方法中添加需要监听的地址以及端口开启服务。
     * 获取当前线程钩子，使jvm关闭前服务同时关闭释放相关资源。
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        // 开启服务
        ChannelFuture channelFuture = nettyServer.start("localhost", 8081);

        // 在JVM销毁前关闭服务
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                nettyServer.close();
            }
        });
        // 这个语句的主要目的是，如果缺失上述代码，则main方法所在的线程，
        // 即主线程会在执行完bind().sync()方法后，会进入finally 代码块，之前的启动的nettyserver也会随之关闭掉，整个程序都结束了。
        // channelFuture.channel().closeFuture().sync();
    }
}
