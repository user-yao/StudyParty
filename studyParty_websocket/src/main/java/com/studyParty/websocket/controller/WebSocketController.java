package com.studyParty.websocket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * WebSocket API文档控制器
 * 提供WebSocket连接相关的API文档
 */
@Tag(name = "WebSocket服务", description = "WebSocket聊天服务相关接口")
@RestController
@RequestMapping("/websocket")
public class WebSocketController {

    @Operation(summary = "WebSocket连接端点", 
               description = "通过该端点建立WebSocket连接，需要在请求头中添加X-User-Id")
    @Parameter(name = "X-User-Id", description = "用户ID，需要在请求头中传递", required = true)
    @RequestMapping("/ws")
    public Mono<String> websocketEndpoint() {
        return Mono.just("WebSocket endpoint. Connect via ws://localhost:8080/websocket/ws with X-User-Id header.");
    }
}