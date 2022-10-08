package com.example.springbootnetty.controller;

import com.example.springbootnetty.netty.client.NettyClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName NettyClientController
 * @Description 客户端controller
 * @Auther tanyi
 * @Date 2022/9/25
 * @Version 1.0
 **/
@RestController
@RequestMapping("/client")
public class NettyClientController {


    /**
     * @description: 模拟向服务器发送消息
     * @param
     * @Author: wuyong
     * @Date: 2019/08/30 14:10:09
     * @return: java.lang.String
     */
    @RequestMapping("/req")
    public String req() {
        String msg = "{\"msgType\":\"req\",\"clientId\":\"请求数据\"}";
        NettyClient.getSocketChannel().writeAndFlush(msg);
        return "success";
    }
}
