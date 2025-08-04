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
    public String updateMarkdown(MultipartFile markdown, Map<String, Source> sourceMap, String processedMarkdown){
        // 替换 Markdown 中的文件引用
        for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
            String originalFilename = entry.getKey();
            String serverUrl = entry.getValue().getUrl();

            // 处理标准 Markdown 图片语法: ![](filename.jpg)
            String imagePattern = "!\\$$(.*?)\\$\\$" + Pattern.quote(originalFilename) + "\\$";
            processedMarkdown = processedMarkdown.replaceAll(imagePattern, "![$1](" + serverUrl + ")");

            // 处理普通链接语法: [](filename.jpg)
            String linkPattern = "\\$$(.*?)\\$\\$" + Pattern.quote(originalFilename) + "\\$";
            processedMarkdown = processedMarkdown.replaceAll(linkPattern, "[$1](" + serverUrl + ")");

            // 处理 HTML 音频标签: <audio src="filename.mp3">
            String audioPattern = "<audio(.*?)src=(\"|')" + Pattern.quote(originalFilename) + "(\"|')(.*?)>";
            processedMarkdown = processedMarkdown.replaceAll(audioPattern, "<audio$1src=\"" + serverUrl + "\"$4>");

            // 处理 HTML 视频标签: <video src="filename.mp4">
            String videoPattern = "<video(.*?)src=(\"|')" + Pattern.quote(originalFilename) + "(\"|')(.*?)>";
            processedMarkdown = processedMarkdown.replaceAll(videoPattern, "<video$1src=\"" + serverUrl + "\"$4>");
        }
        return processedMarkdown;
    }

}