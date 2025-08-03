package com.studyParty.websocket.controller;

import com.studyParty.websocket.model.WebSocketMessage;
import com.studyParty.websocket.service.WebSocketMessageService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String receiverId,
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String senderId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
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
            String fileUrl = "/file/download/" + uniqueFilename;
            
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
                
                // 发送文件消息
                webSocketMessageService.sendFileMessage(message);
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

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
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