package com.studyParty.websocket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyParty.websocket.model.WebSocketMessage;
import com.studyParty.websocket.handler.ChatWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户会话管理器
 * 用于管理WebSocket连接的用户会话
 */
@Service
public class UserSessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(UserSessionManager.class);
    
    @Autowired
    private RedisUtil redisUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 存储用户ID与WebSocket会话的映射关系
    private final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    // 存储用户最后活动时间
    private final ConcurrentHashMap<String, Long> userLastActiveTime = new ConcurrentHashMap<>();
    
    // 心跳超时时间（30秒）
    private static final long HEARTBEAT_TIMEOUT = 30000;
    
    /**
     * 添加用户会话
     * 
     * @param userId 用户ID
     * @param session WebSocket会话
     */
    public void addUserSession(String userId, WebSocketSession session) {
        userSessions.put(userId, session);
        userLastActiveTime.put(userId, System.currentTimeMillis());
        // 设置用户在线状态
        redisUtil.setUserOnline(userId).subscribe();
        // 发送离线消息
        sendOfflineMessages(userId, session);
    }
    
    /**
     * 移除用户会话
     * 
     * @param userId 用户ID
     */
    public void removeUserSession(String userId) {
        userSessions.remove(userId);
        userLastActiveTime.remove(userId);
        // 设置用户离线状态
        redisUtil.setUserOffline(userId).subscribe();
    }
    
    /**
     * 更新用户最后活动时间
     * 
     * @param userId 用户ID
     */
    public void updateLastActiveTime(String userId) {
        userLastActiveTime.put(userId, System.currentTimeMillis());
    }
    
    /**
     * 根据用户ID获取WebSocket会话
     * 
     * @param userId 用户ID
     * @return WebSocket会话
     */
    public WebSocketSession getUserSession(String userId) {
        return userSessions.get(userId);
    }
    
    /**
     * 获取在线用户数量
     * 
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }
    
    /**
     * 获取所有在线用户ID
     * 
     * @return 所有在线用户ID数组
     */
    public String[] getOnlineUserIds() {
        return userSessions.keySet().toArray(new String[0]);
    }
    
    /**
     * 向指定用户发送消息（如果用户在线则直接发送，否则存储为离线消息）
     * 
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendMessageToUser(String userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                // 检查是否是文件消息
                WebSocketMessage wsMessage = objectMapper.readValue(message, WebSocketMessage.class);
                if (wsMessage.getFileUrl() != null && !wsMessage.getFileUrl().isEmpty()) {
                    // 发送二进制消息
                    sendBinaryMessage(session, wsMessage);
                } else {
                    // 发送文本消息
                    session.sendMessage(new TextMessage(message));
                }
                // 更新最后活动时间
                updateLastActiveTime(userId);
            } catch (Exception e) {
                logger.error("Error sending message to user {}: {}", userId, e.getMessage(), e);
                // 发送失败，认为用户已离线
                removeUserSession(userId);
                redisUtil.saveOfflineMessage(userId, message).subscribe();
            }
        } else {
            // 用户不在线，存储为离线消息
            redisUtil.saveOfflineMessage(userId, message).subscribe();
        }
    }
    
    /**
     * 向指定群组发送消息
     * 
     * @param groupId 群组ID
     * @param message 消息内容
     */
    public void sendMessageToGroup(String groupId, String message) {
        redisUtil.getGroupMembers(groupId)
            .flatMap(members -> {
                for (String userId : members) {
                    sendMessageToUser(userId, message);
                }
                return Mono.empty();
            })
            .subscribe();
    }
    
    /**
     * 向所有用户广播消息
     * 
     * @param message 消息内容
     */
    public void broadcastMessage(String message) {
        userSessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    String userId = getUserIdBySession(session);
                    // 检查是否是文件消息
                    WebSocketMessage wsMessage = objectMapper.readValue(message, WebSocketMessage.class);
                    if (wsMessage.getFileUrl() != null && !wsMessage.getFileUrl().isEmpty()) {
                        // 发送二进制消息
                        sendBinaryMessage(session, wsMessage);
                    } else {
                        // 发送文本消息
                        session.sendMessage(new TextMessage(message));
                    }
                    // 更新最后活动时间
                    updateLastActiveTime(userId);
                } catch (Exception e) {
                    String userId = getUserIdBySession(session);
                    logger.error("Error broadcasting message to user {}: {}", userId, e.getMessage(), e);
                    // 发送失败，认为用户已离线
                    removeUserSession(userId);
                }
            }
        });
    }
    
    /**
     * 根据会话获取用户ID
     * 
     * @param session WebSocket会话
     * @return 用户ID
     */
    private String getUserIdBySession(WebSocketSession session) {
        return userSessions.entrySet().stream()
                .filter(entry -> entry.getValue() == session)
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 发送离线消息给用户
     * 
     * @param userId 用户ID
     * @param session WebSocket会话
     */
    private void sendOfflineMessages(String userId, WebSocketSession session) {
        redisUtil.getOfflineMessages(userId)
            .flatMap(messages -> {
                // 反转消息列表以确保按正确的时间顺序发送
                java.util.Collections.reverse(messages);
                for (String message : messages) {
                    if (!message.isEmpty()) {
                        try {
                            // 添加离线消息标识
                            WebSocketMessage offlineMsg = objectMapper.readValue(message, WebSocketMessage.class);
                            offlineMsg.setType("offline_" + offlineMsg.getType());
                            offlineMsg.setOfflineMessage(true);
                            
                            // 检查是否是文件消息
                            if (offlineMsg.getFileUrl() != null && !offlineMsg.getFileUrl().isEmpty()) {
                                // 发送二进制消息
                                sendBinaryMessage(session, offlineMsg);
                            } else {
                                // 发送文本消息
                                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(offlineMsg)));
                            }
                        } catch (IOException e) {
                            // 如果解析失败，直接发送原始消息
                            try {
                                session.sendMessage(new TextMessage("[离线消息] " + message));
                            } catch (IOException ioException) {
                                logger.error("Error sending offline message to user {}: {}", userId, ioException.getMessage(), ioException);
                            }
                        } catch (Exception e) {
                            logger.error("Error processing offline message for user {}: {}", userId, e.getMessage(), e);
                        }
                    }
                }
                // 删除已发送的离线消息
                return redisUtil.deleteOfflineMessages(userId);
            })
            .subscribe();
    }
    
    /**
     * 发送二进制消息
     * 
     * @param session WebSocket会话
     * @param message WebSocket消息
     * @throws IOException IO异常
     */
    private void sendBinaryMessage(WebSocketSession session, WebSocketMessage message) throws IOException {
        // 创建二进制消息
        java.nio.ByteBuffer buffer = ChatWebSocketHandler.createBinaryMessage(message);
        
        // 发送二进制消息
        session.sendMessage(new BinaryMessage(buffer));
    }
    
    /**
     * 定期检查用户连接状态
     * 每10秒执行一次
     */
    @Scheduled(fixedRate = 10000)
    public void checkUserConnections() {
        long currentTime = System.currentTimeMillis();
        for (String userId : userSessions.keySet()) {
            Long lastActiveTime = userLastActiveTime.get(userId);
            if (lastActiveTime != null && (currentTime - lastActiveTime) > HEARTBEAT_TIMEOUT) {
                logger.info("User {} connection timeout, removing session", userId);
                WebSocketSession session = userSessions.get(userId);
                if (session != null) {
                    try {
                        session.close();
                    } catch (IOException e) {
                        logger.error("Error closing session for user {}: {}", userId, e.getMessage(), e);
                    }
                }
                removeUserSession(userId);
            }
        }
    }
}