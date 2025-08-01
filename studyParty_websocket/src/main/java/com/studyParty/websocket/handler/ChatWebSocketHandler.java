package com.studyParty.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyParty.websocket.model.WebSocketMessage;
import com.studyParty.websocket.service.UserSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 聊天WebSocket处理器
 * 处理WebSocket连接、消息和断开连接等事件
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    
    @Autowired
    private UserSessionManager userSessionManager;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 存储所有活跃的WebSocket会话
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    
    /**
     * 处理WebSocket连接建立后的操作
     *
     * @param session WebSocket会话
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket connection established");
        
        // 从请求头中获取用户ID
        String userId = session.getHandshakeHeaders().getFirst("X-User-Id");
        
        logger.info("User ID from header: {}", userId);
        
        if (userId == null || userId.isEmpty()) {
            // 如果没有提供用户ID，关闭连接
            logger.warn("No user ID provided in headers, closing connection");
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        
        // 将用户会话添加到管理器中
        userSessionManager.addUserSession(userId, session);
        sessions.add(session);
        logger.info("User {} connected successfully", userId);
        
        // 发送欢迎消息
        WebSocketMessage welcomeMessage = new WebSocketMessage();
        welcomeMessage.setType("welcome");
        welcomeMessage.setContent("欢迎连接到聊天服务器，用户ID: " + userId);
        welcomeMessage.setTimestamp(Instant.now().toEpochMilli());
        
        // 发送在线用户列表
        WebSocketMessage onlineUsersMessage = new WebSocketMessage();
        onlineUsersMessage.setType("online_users");
        onlineUsersMessage.setOnlineUsers(userSessionManager.getOnlineUserIds());
        onlineUsersMessage.setTimestamp(Instant.now().toEpochMilli());
        
        // 发送消息给当前用户
        session.sendMessage(new TextMessage(toJson(welcomeMessage)));
        session.sendMessage(new TextMessage(toJson(onlineUsersMessage)));
        
        // 广播用户上线消息
        WebSocketMessage userJoinedMessage = new WebSocketMessage();
        userJoinedMessage.setType("user_joined");
        userJoinedMessage.setContent("用户 " + userId + " 加入了聊天");
        userJoinedMessage.setSenderId(userId);
        userJoinedMessage.setTimestamp(Instant.now().toEpochMilli());
        userJoinedMessage.setOnlineUsers(userSessionManager.getOnlineUserIds());
        
        broadcastMessage(toJson(userJoinedMessage));
    }
    
    /**
     * 处理接收到的文本消息
     *
     * @param session WebSocket会话
     * @param message 接收到的文本消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = session.getHandshakeHeaders().getFirst("X-User-Id");
        logger.debug("Received message from user {}: {}", userId, message.getPayload());
        
        try {
            String payload = message.getPayload();
            
            // 解析消息
            WebSocketMessage receivedMessage = objectMapper.readValue(payload, WebSocketMessage.class);
            receivedMessage.setSenderId(userId);
            receivedMessage.setTimestamp(Instant.now().toEpochMilli());
            
            logger.debug("Parsed message from user {}: {}", userId, payload);
            
            if ("ping".equals(receivedMessage.getContent())) {
                // 处理心跳消息
                logger.debug("Processing ping message from user {}", userId);
                WebSocketMessage pongMessage = new WebSocketMessage();
                pongMessage.setType("pong");
                pongMessage.setContent("pong");
                pongMessage.setTimestamp(Instant.now().toEpochMilli());
                session.sendMessage(new TextMessage(toJson(pongMessage)));
            } else if (receivedMessage.getReceiverId() != null && !receivedMessage.getReceiverId().isEmpty()) {
                // 发送私聊消息
                logger.debug("Processing private message from user {} to user {}", userId, receivedMessage.getReceiverId());
                WebSocketMessage privateMessage = new WebSocketMessage();
                privateMessage.setType("private_message");
                privateMessage.setContent(receivedMessage.getContent());
                privateMessage.setSenderId(userId);
                privateMessage.setReceiverId(receivedMessage.getReceiverId());
                privateMessage.setTimestamp(Instant.now().toEpochMilli());
                
                // 发送给接收者
                sendMessageToUser(receivedMessage.getReceiverId(), toJson(privateMessage));
                
                // 发送回执给发送者
                session.sendMessage(new TextMessage(toJson(privateMessage)));
            } else {
                // 广播消息
                logger.debug("Processing public message from user {}", userId);
                receivedMessage.setType("public_message");
                broadcastMessage(toJson(receivedMessage));
            }
        } catch (Exception e) {
            // 处理解析错误
            logger.error("Error parsing message from user {}: {}", userId, e.getMessage(), e);
            WebSocketMessage errorMessage = new WebSocketMessage();
            errorMessage.setType("error");
            errorMessage.setContent("消息解析错误: " + e.getMessage());
            errorMessage.setTimestamp(Instant.now().toEpochMilli());
            session.sendMessage(new TextMessage(toJson(errorMessage)));
        }
    }
    
    /**
     * 处理WebSocket连接关闭后的操作
     *
     * @param session     WebSocket会话
     * @param closeStatus 关闭状态
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String userId = session.getHandshakeHeaders().getFirst("X-User-Id");
        logger.info("User {} disconnected with status: {}", userId, closeStatus);
        
        // 从会话管理器中移除用户
        userSessionManager.removeUserSession(userId);
        sessions.remove(session);
        
        // 广播用户离线消息
        WebSocketMessage userLeftMessage = new WebSocketMessage();
        userLeftMessage.setType("user_left");
        userLeftMessage.setContent("用户 " + userId + " 离开了聊天");
        userLeftMessage.setSenderId(userId);
        userLeftMessage.setTimestamp(Instant.now().toEpochMilli());
        userLeftMessage.setOnlineUsers(userSessionManager.getOnlineUserIds());
        
        broadcastMessage(toJson(userLeftMessage));
    }
    
    /**
     * 处理传输错误
     *
     * @param session   WebSocket会话
     * @param exception 异常
     * @throws Exception 异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String userId = session.getHandshakeHeaders().getFirst("X-User-Id");
        logger.error("Transport error for user {}: {}", userId, exception.getMessage(), exception);
        
        // 从会话管理器中移除用户
        userSessionManager.removeUserSession(userId);
        sessions.remove(session);
    }
    
    /**
     * 向指定用户发送消息
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    private void sendMessageToUser(String userId, String message) {
        try {
            WebSocketSession session = userSessionManager.getUserSession(userId);
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (Exception e) {
            logger.error("Error sending message to user {}: {}", userId, e.getMessage(), e);
        }
    }
    
    /**
     * 向所有用户广播消息
     *
     * @param message 消息内容
     */
    private void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (Exception e) {
                String userId = session.getHandshakeHeaders().getFirst("X-User-Id");
                logger.error("Error broadcasting message to user {}: {}", userId, e.getMessage(), e);
                
                // 移除已关闭的会话
                sessions.remove(session);
                String uid = session.getHandshakeHeaders().getFirst("X-User-Id");
                if (uid != null) {
                    userSessionManager.removeUserSession(uid);
                }
            }
        }
    }
    
    /**
     * 将对象转换为JSON字符串
     *
     * @param object 对象
     * @return JSON字符串
     */
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Error converting object to JSON: {}", e.getMessage(), e);
            return "{}";
        }
    }
}