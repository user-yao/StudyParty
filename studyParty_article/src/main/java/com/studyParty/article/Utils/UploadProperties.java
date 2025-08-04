package com.studyParty.article.Utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传配置属性类
 * 用于读取application.yml中的上传配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    /**
     * 文件存储基础路径
     */
    private String basePath;

    /**
     * 文件访问基础URL
     */
    private String baseUrl;

    /**
     * 允许的文件类型，多个类型用逗号分隔
     */
    private String allowedTypes;

    /**
     * 统一默认最大大小(MB)
     */
    private Long maxSize;

    /**
     * 按类型设置最大大小(MB)，例如：image=10, audio=50, video=200
     */
    private Map<String, Long> maxSizes = new HashMap<>();
}