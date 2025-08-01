package com.studyParty.websocket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户会话管理器
 * 用于管理WebSocket连接的用户会话
 */
@Service
public class UserSessionManager {
    
    // 存储用户ID与WebSocket会话的映射关系
    private final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    /**
     * 添加用户会话
     * 
     * @param userId 用户ID
     * @param session WebSocket会话
     */
    public void addUserSession(String userId, WebSocketSession session) {
        userSessions.put(userId, session);
    }
    
    /**
     * 移除用户会话
     * 
     * @param userId 用户ID
     */
    public void removeUserSession(String userId) {
        userSessions.remove(userId);
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
     * 向指定用户发送消息
     * 
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void sendMessageToUser(String userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                logger.error("Error sending message to user {}: {}", userId, e.getMessage(), e);
            }
        }
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
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    String userId = getUserIdBySession(session);
                    logger.error("Error broadcasting message to user {}: {}", userId, e.getMessage(), e);
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
    
    private static final Logger logger = LoggerFactory.getLogger(UserSessionManager.class);
}