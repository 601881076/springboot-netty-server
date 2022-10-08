package com.example.springbootnetty.controller;

import com.example.springbootnetty.netty.server.ServerHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName NettyServerController
 * @Description spring boot netty controller
 * @Auther tanyi
 * @Date 2022/9/25
 * @Version 1.0
 **/

@RestController
@RequestMapping("/netty/server")
public class NettyServerController {

    /**
     * 获取当前正在连接的客户端信息
     * @return
     */
    @GetMapping("/clientList")
    public Map<String, ServerHandler.NettyClient> getClientList() {
        return ServerHandler.NettyChannelMap.clientMap;
    }
}
