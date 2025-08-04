package com.studyParty.article.Utils;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理器
 * 统一处理系统中的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理文件大小超出限制异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("文件大小超出限制", e);
        String message = "上传文件大小超出限制";
        ApiError apiError = new ApiError(HttpStatus.PAYLOAD_TOO_LARGE.value(), message);
        return new ResponseEntity<>(apiError, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception e) {
        log.error("系统异常", e);
        String message = StrUtil.isNotBlank(e.getMessage()) ? e.getMessage() : "系统内部错误";
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * API错误响应实体
     */
    @Data
    public static class ApiError {
        private int code;
        private String message;

        public ApiError(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
