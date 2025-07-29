package com.studyparty.group.Utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件上传工具类
 * 负责处理文件上传、存储和访问URL生成
 */
@Slf4j
public class FileUploadUtil {

    // 获取配置属性
    private static final UploadProperties uploadProperties = SpringUtil.getBean(UploadProperties.class);

    // 允许的文件类型集合
    private static final Set<String> ALLOWED_TYPES = Stream.of(
                    uploadProperties.getAllowedTypes().split(","))
            .map(String::trim)
            .collect(Collectors.toSet());

    /**
     * 上传图片文件
     *
     * @param file 上传的文件
     * @return 图片访问URL
     */
    public static String uploadImage(MultipartFile file) {
        // 验证文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("不支持的文件类型: " + contentType +
                    ", 允许的类型: " + uploadProperties.getAllowedTypes());
        }

        // 验证文件大小
        long fileSize = file.getSize();
        long maxSize = uploadProperties.getMaxSize() * 1024 * 1024; // 转换为字节
        if (fileSize > maxSize) {
            throw new BusinessException("文件大小超过限制: " + uploadProperties.getMaxSize() + "MB");
        }

        try {
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtil.extName(originalFilename);

            // 生成唯一文件名(UUID)
            String fileName = IdUtil.simpleUUID() + "." + extension;

            // 按日期生成目录结构 (例如: 2023/10/25/)
            String dateDir = new SimpleDateFormat("yyyy/MM/dd/").format(new Date());

            // 构建完整存储路径
            String basePath = uploadProperties.getBasePath();
            String filePath = basePath + dateDir + fileName;

            // 创建目录(如果不存在)
            File saveFile = new File(filePath);
            if (!saveFile.getParentFile().exists()) {
                boolean mkdirs = saveFile.getParentFile().mkdirs();
                if (!mkdirs) {
                    throw new BusinessException("创建目录失败: " + saveFile.getParentFile().getPath());
                }
            }

            // 保存文件到磁盘
            file.transferTo(saveFile);
            log.info("文件上传成功: {}", filePath);

            // 生成访问URL
            return uploadProperties.getBaseUrl() + dateDir + fileName;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param url 文件访问URL
     * @return 是否删除成功
     */
    public static boolean deleteFile(String url) {
        if (StrUtil.isEmpty(url)) {
            return false;
        }

        // 从URL中提取文件路径
        String baseUrl = uploadProperties.getBaseUrl();
        if (!url.startsWith(baseUrl)) {
            log.warn("不是本系统上传的文件，无法删除: {}", url);
            return false;
        }

        String relativePath = url.substring(baseUrl.length());
        String filePath = uploadProperties.getBasePath() + relativePath;

        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
                log.info("文件删除成功: {}", filePath);
                return true;
            } catch (IOException e) {
                log.error("文件删除失败", e);
                return false;
            }
        }

        log.warn("文件不存在，无法删除: {}", filePath);
        return false;
    }
}