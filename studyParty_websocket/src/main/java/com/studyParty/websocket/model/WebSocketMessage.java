package com.studyParty.websocket.model;

import lombok.Data;

/**
 * WebSocket消息实体类
 * 用于封装WebSocket通信的消息格式
 */
@Data
public class WebSocketMessage {
    // 消息类型 (text, image, voice, video, file, private_message, group_message, etc.)
    private String type;
    
    // 消息内容
    private String content;
    
    // 发送者ID
    private String senderId;
    
    // 接收者ID（用于私聊）
    private String receiverId;
    
    // 群组ID（用于群聊）
    private String groupId;
    
    // 时间戳
    private Long timestamp;
    
    // 在线用户列表（用于用户上线/下线通知）
    private String[] onlineUsers;
    
    // 文件相关信息
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType; // 文件类型: image, voice, video, document等
    
    // 文件描述信息
    private String fileDescription;
    
    // 是否为离线消息
    private boolean offlineMessage = false;
}