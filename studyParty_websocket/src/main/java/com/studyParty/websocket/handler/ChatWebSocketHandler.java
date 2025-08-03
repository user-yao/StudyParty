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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
                
                // 更新用户最后活动时间
                userSessionManager.updateLastActiveTime(userId);
            } else if (receivedMessage.getGroupId() != null && !receivedMessage.getGroupId().isEmpty()) {
                // 处理群组消息
                logger.debug("Processing group message from user {} to group {}", userId, receivedMessage.getGroupId());
                receivedMessage.setType(getMessageType(receivedMessage) + "_group");

                // 发送给群组中的所有用户
                userSessionManager.sendMessageToGroup(receivedMessage.getGroupId(), toJson(receivedMessage));
            } else if (receivedMessage.getReceiverId() != null && !receivedMessage.getReceiverId().isEmpty()) {
                // 发送私聊消息
                logger.debug("Processing private message from user {} to user {}", userId, receivedMessage.getReceiverId());
                receivedMessage.setType(getMessageType(receivedMessage) + "_private");

                // 发送给接收者
                userSessionManager.sendMessageToUser(receivedMessage.getReceiverId(), toJson(receivedMessage));

                // 发送回执给发送者
                session.sendMessage(new TextMessage(toJson(receivedMessage)));
            } else {
                // 广播消息
                logger.debug("Processing public message from user {}", userId);
                receivedMessage.setType(getMessageType(receivedMessage) + "_public");
                userSessionManager.broadcastMessage(toJson(receivedMessage));
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
     * 处理接收到的二进制消息
     *
     * @param session WebSocket会话
     * @param message 接收到的二进制消息
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String userId = session.getHandshakeHeaders().getFirst("X-User-Id");
        logger.debug("Received binary message from user {}: {} bytes", userId, message.getPayloadLength());
        
        try {
            // 检查消息大小
            if (message.getPayloadLength() > 65536) {
                // 消息过大，建议客户端使用文件上传方式
                logger.warn("Binary message from user {} is too large: {} bytes", userId, message.getPayloadLength());
                WebSocketMessage errorMessage = new WebSocketMessage();
                errorMessage.setType("error");
                errorMessage.setContent("文件过大，请使用文件上传接口上传文件，然后通过WebSocket发送文件链接");
                errorMessage.setTimestamp(Instant.now().toEpochMilli());
                session.sendMessage(new TextMessage(toJson(errorMessage)));
                return;
            }
            
            // 解析二进制消息
            WebSocketMessage receivedMessage = parseBinaryMessage(message.getPayload());
            
            receivedMessage.setSenderId(userId);
            receivedMessage.setTimestamp(Instant.now().toEpochMilli());
            
            logger.debug("Parsed binary message from user {}: {}", userId, receivedMessage.getFileName());
            
            if (receivedMessage.getGroupId() != null && !receivedMessage.getGroupId().isEmpty()) {
                // 处理群组文件消息
                logger.debug("Processing group file message from user {} to group {}", userId, receivedMessage.getGroupId());
                receivedMessage.setType(getMessageType(receivedMessage) + "_group");

                // 发送给群组中的所有用户
                userSessionManager.sendMessageToGroup(receivedMessage.getGroupId(), toJson(receivedMessage));
            } else if (receivedMessage.getReceiverId() != null && !receivedMessage.getReceiverId().isEmpty()) {
                // 发送私聊文件消息
                logger.debug("Processing private file message from user {} to user {}", userId, receivedMessage.getReceiverId());
                receivedMessage.setType(getMessageType(receivedMessage) + "_private");

                // 发送给接收者
                userSessionManager.sendMessageToUser(receivedMessage.getReceiverId(), toJson(receivedMessage));

                // 发送回执给发送者
                session.sendMessage(new TextMessage(toJson(receivedMessage)));
            } else {
                // 广播文件消息
                logger.debug("Processing public file message from user {}", userId);
                receivedMessage.setType(getMessageType(receivedMessage) + "_public");
                userSessionManager.broadcastMessage(toJson(receivedMessage));
            }
        } catch (Exception e) {
            // 处理解析错误
            logger.error("Error parsing binary message from user {}: {}", userId, e.getMessage(), e);
            try {
                WebSocketMessage errorMessage = new WebSocketMessage();
                errorMessage.setType("error");
                errorMessage.setContent("二进制消息解析错误: " + e.getMessage());
                errorMessage.setTimestamp(Instant.now().toEpochMilli());
                session.sendMessage(new TextMessage(toJson(errorMessage)));
            } catch (Exception ex) {
                logger.error("Error sending error message to user {}: {}", userId, ex.getMessage(), ex);
            }
        }
    }
    
    /**
     * 解析二进制消息
     * 格式: [元数据长度(4字节)][元数据JSON][文件数据]
     * 
     * @param buffer 二进制数据
     * @return WebSocket消息对象
     * @throws IOException IO异常
     */
    private WebSocketMessage parseBinaryMessage(ByteBuffer buffer) throws IOException {
        // 读取元数据长度
        int metadataLength = buffer.getInt();
        
        // 读取元数据
        byte[] metadataBytes = new byte[metadataLength];
        buffer.get(metadataBytes);
        String metadataJson = new String(metadataBytes, StandardCharsets.UTF_8);
        
        // 解析元数据
        WebSocketMessage message = objectMapper.readValue(metadataJson, WebSocketMessage.class);
        
        // 注意：现在我们不再处理文件数据，只传输文件URL
        
        return message;
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

    /**
     * 根据消息内容确定消息类型
     *
     * @param message 消息对象
     * @return 消息类型
     */
    private String getMessageType(WebSocketMessage message) {
        if (message.getFileType() != null && !message.getFileType().isEmpty()) {
            return message.getFileType(); // image, voice, video, document等
        }
        return "text"; // 默认文本消息
    }
    
    /**
     * 创建二进制消息
     * 格式: [元数据长度(4字节)][元数据JSON][文件数据]
     * 
     * @param message WebSocket消息
     * @return 二进制数据
     * @throws IOException IO异常
     */
    public static ByteBuffer createBinaryMessage(WebSocketMessage message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 创建元数据JSON
        String metadataJson = objectMapper.writeValueAsString(message);
        byte[] metadataBytes = metadataJson.getBytes(StandardCharsets.UTF_8);
        
        // 计算总长度 (现在不包含文件数据)
        int totalLength = 4 + metadataBytes.length;
        
        // 创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        
        // 写入元数据长度
        buffer.putInt(metadataBytes.length);
        
        // 写入元数据
        buffer.put(metadataBytes);
        
        // 准备读取
        buffer.flip();
        
        return buffer;
    }
}