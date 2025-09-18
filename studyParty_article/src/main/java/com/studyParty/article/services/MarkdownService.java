package com.studyParty.article.services;

import com.studyParty.entity.Source;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Markdown服务类
 * 处理Markdown转HTML的业务逻辑
 */
@Service
public class MarkdownService {
    public String checkMarkdown( MultipartFile markdown){
        // 1. 检查文件类型
        if (!"text/markdown".equals(markdown.getContentType()) &&
                !Objects.requireNonNull(markdown.getOriginalFilename()).endsWith(".md")) {
            return null;
        }
        // 2. 读取文件内容
        String processedMarkdown = null;
        try {
            processedMarkdown = new String(markdown.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
        return processedMarkdown;
    }
    public String updateMarkdown( Map<String, Source> sourceMap, String processedMarkdown){
        // 替换 Markdown 中的文件引用
        for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
            String originalFilename = entry.getKey();
            String serverUrl = entry.getValue().getUrl();

            // 提取文件名（不包含路径）
            String simpleFilename = originalFilename;
            if (originalFilename.contains("\\")) {
                simpleFilename = originalFilename.substring(originalFilename.lastIndexOf('\\') + 1);
            } else if (originalFilename.contains("/")) {
                simpleFilename = originalFilename.substring(originalFilename.lastIndexOf('/') + 1);
            }

            // 转义特殊字符用于正则表达式
            String escapedOriginalFilename = Pattern.quote(originalFilename);
            String escapedSimpleFilename = Pattern.quote(simpleFilename);

            // 处理标准 Markdown 图片语法: ![](filename.jpg) 或 ![](path/filename.jpg)
            // 使用捕获组来保留alt文本
            processedMarkdown = processedMarkdown.replaceAll(
                    "!\\[([^\\]]*)\\]\\([^\\)]*" + escapedOriginalFilename + "[^\\)]*\\)",
                    "![$1](" + serverUrl + ")"
            );

            processedMarkdown = processedMarkdown.replaceAll(
                    "!\\[([^\\]]*)\\]\\([^\\)]*" + escapedSimpleFilename + "[^\\)]*\\)",
                    "![$1](" + serverUrl + ")"
            );

            // 处理普通链接语法: [](filename.jpg)
            processedMarkdown = processedMarkdown.replaceAll(
                    "\\[([^\\]]*)\\]\\([^\\)]*" + escapedOriginalFilename + "[^\\)]*\\)",
                    "[$1](" + serverUrl + ")"
            );

            processedMarkdown = processedMarkdown.replaceAll(
                    "\\[([^\\]]*)\\]\\([^\\)]*" + escapedSimpleFilename + "[^\\)]*\\)",
                    "[$1](" + serverUrl + ")"
            );

            // 处理 HTML 音频标签: <audio src="filename.mp3">
            processedMarkdown = processedMarkdown.replaceAll(
                    "<audio([^>]*?)src=[\"'][^\"']*" + escapedOriginalFilename + "[^\"']*[\"']([^>]*?)>",
                    "<audio$1src=\"" + serverUrl + "\"$2>"
            );

            processedMarkdown = processedMarkdown.replaceAll(
                    "<audio([^>]*?)src=[\"'][^\"']*" + escapedSimpleFilename + "[^\"']*[\"']([^>]*?)>",
                    "<audio$1src=\"" + serverUrl + "\"$2>"
            );

            // 处理 HTML 视频标签: <video src="filename.mp4">
            processedMarkdown = processedMarkdown.replaceAll(
                    "<video([^>]*?)src=[\"'][^\"']*" + escapedOriginalFilename + "[^\"']*[\"']([^>]*?)>",
                    "<video$1src=\"" + serverUrl + "\"$2>"
            );

            processedMarkdown = processedMarkdown.replaceAll(
                    "<video([^>]*?)src=[\"'][^\"']*" + escapedSimpleFilename + "[^\"']*[\"']([^>]*?)>",
                    "<video$1src=\"" + serverUrl + "\"$2>"
            );
        }
        return processedMarkdown;
    }

}