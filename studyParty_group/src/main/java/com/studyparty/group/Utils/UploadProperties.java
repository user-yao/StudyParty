package com.studyparty.group.Utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
     * 最大文件大小(MB)
     */
    private Integer maxSize;
}