package com.studyParty.websocket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyParty.websocket.model.WebSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSocketMessageService {
    
    @Autowired
    private UserSessionManager userSessionManager;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 发送文件消息
     * 
     * @param message WebSocket消息对象
     */
    public void sendFileMessage(WebSocketMessage message) {
        if (message.getGroupId() != null && !message.getGroupId().isEmpty()) {
            // 群组消息
            userSessionManager.sendMessageToGroup(message.getGroupId(), messageToJson(message));
        } else if (message.getReceiverId() != null && !message.getReceiverId().isEmpty()) {
            // 私聊消息
            userSessionManager.sendMessageToUser(message.getReceiverId(), messageToJson(message));
        } else {
            // 广播消息
            userSessionManager.broadcastMessage(messageToJson(message));
        }
    }
    
    /**
     * 将WebSocketMessage对象转换为JSON字符串
     * 
     * @param message WebSocket消息对象
     * @return JSON字符串
     */
    private String messageToJson(WebSocketMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            return "{}";
        }
    }
}