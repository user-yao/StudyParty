package com.studyParty.websocket.controller;

import com.studyParty.websocket.model.WebSocketMessage;
import com.studyParty.websocket.service.WebSocketMessageService;
import com.studyParty.websocket.service.UserSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/websocket")
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Autowired
    private WebSocketMessageService webSocketMessageService;
    
    @Autowired
    private UserSessionManager userSessionManager;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String receiverId,
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String content,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String senderId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 获取当前日期作为目录名
            String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
            Path uploadPath = Paths.get(uploadDir + "/" + dateDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名，使用UUID确保唯一性
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 构建文件访问URL
            String fileUrl = "/static/websocket/" + dateDir +"/"+ uniqueFilename;

            // 如果提供了发送者ID，则自动发送文件消息
            if (senderId != null && !senderId.isEmpty()) {
                WebSocketMessage message = new WebSocketMessage();
                message.setType(fileType != null ? fileType : "file");
                message.setContent(content != null ? content : "发送了一个文件");
                message.setSenderId(senderId);
                message.setReceiverId(receiverId);
                message.setGroupId(groupId);
                message.setFileName(originalFilename);
                message.setFileUrl(fileUrl);
                message.setFileSize(file.getSize());
                message.setFileType(fileType);
                message.setTimestamp(System.currentTimeMillis());

                // 发送文件消息给接收者
                webSocketMessageService.sendFileMessage(message);
                
                // 发送回执给发送者（和ChatWebSocketHandler中实现的一样）
                WebSocketMessage receiptMessage = new WebSocketMessage();
                receiptMessage.setType(message.getType());
                receiptMessage.setContent(message.getContent());
                receiptMessage.setSenderId(message.getSenderId());
                receiptMessage.setReceiverId(message.getReceiverId());
                receiptMessage.setGroupId(message.getGroupId());
                receiptMessage.setFileName(message.getFileName());
                receiptMessage.setFileUrl(message.getFileUrl());
                receiptMessage.setFileSize(message.getFileSize());
                receiptMessage.setFileType(message.getFileType());
                receiptMessage.setTimestamp(message.getTimestamp());
                
                // 直接给发送者发送回执消息
                userSessionManager.sendMessageToUser(senderId, toJson(receiptMessage));
            }

            // 返回文件信息
            response.put("success", true);
            response.put("filename", uniqueFilename);
            response.put("originalFilename", originalFilename);
            response.put("size", file.getSize());
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("error", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // 将WebSocketMessage对象转换为JSON字符串
    private String toJson(WebSocketMessage message) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(message);
        } catch (Exception e) {
            return "{}";
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(String userId,String filename) {
        try {
            filename = userId +"/"+ filename;
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}