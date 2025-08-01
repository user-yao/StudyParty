package com.studyParty.websocket.model;

import lombok.Data;

/**
 * WebSocket消息实体类
 * 用于封装WebSocket通信的消息格式
 */
@Data
public class WebSocketMessage {
    // 消息类型
    private String type;
    
    // 消息内容
    private String content;
    
    // 发送者ID
    private String senderId;
    
    // 接收者ID（用于私聊）
    private String receiverId;
    
    // 时间戳
    private Long timestamp;
    
    // 在线用户列表（用于用户上线/下线通知）
    private String[] onlineUsers;
}