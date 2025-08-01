package com.studyParty.websocket.config;

import com.studyParty.websocket.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类
 * 用于注册WebSocket处理器和配置URL映射
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * 注册WebSocket处理器
     *
     * @param registry WebSocket处理器注册器
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册聊天处理器，指定处理路径为 /ws 和 /websocket/ws
        registry.addHandler(chatWebSocketHandler(), "/ws", "/websocket/ws")
                .setAllowedOrigins("*"); // 允许跨域访问
    }

    /**
     * 创建WebSocket处理器Bean
     *
     * @return ChatWebSocketHandler实例
     */
    @Bean
    public ChatWebSocketHandler chatWebSocketHandler() {
        return new ChatWebSocketHandler();
    }
}