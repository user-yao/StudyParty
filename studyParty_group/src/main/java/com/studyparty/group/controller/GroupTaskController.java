package com.studyparty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.entity.group.GroupTask;
import com.studyparty.group.common.Result;
import com.studyparty.group.mapper.GroupTaskMapper;
import com.studyparty.group.services.ImageServer;
import com.studyparty.group.services.MarkdownService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController("/groupTask")
public class GroupTaskController {
    @Autowired
    private  GroupTaskMapper groupTaskMapper;
    @Autowired
    private ImageServer imageServer;
    @Autowired
    private MarkdownService markdownService;

    @PostMapping("/selectMyGroupTask")
    public Result<?> selectMyGroupTask( String groupId, Integer currentPage) {
        if (groupId == null) {
            return Result.error("参数错误");
        }
        if (currentPage == null || currentPage < 1) {
            currentPage = 1;
        }
        Page<GroupTask> page = new Page<>(currentPage, 10);
        QueryWrapper<GroupTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("groupId", groupId);
        return Result.success(groupTaskMapper.selectPage(page, queryWrapper));
    }
    @PostMapping("/uploadImage")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile[] file,
                                              @RequestParam(value = "postId", required = false) Long groupTaskId) {
        imageServer.uploadImage(file, groupTaskId);
        return Result.success();
    }
    @PostMapping
    public Result<?> createPost(@RequestBody GroupTask groupTask) {
        int createdPost = groupTaskMapper.insert(groupTask);
        return Result.success(createdPost);
    }
    @DeleteMapping("/{id}")
    public Result<?> deletePost(@PathVariable Long id) {
        groupTaskMapper.deleteById(id);
        imageServer.deleteImage(id);
        return Result.success();
    }

    @PostMapping("/upload-markdown")
    public Result<?> uploadMarkdownFile(@RequestParam("file") MultipartFile file){
        // 1. 检查文件类型
        if (!"text/markdown".equals(file.getContentType()) &&
                !file.getOriginalFilename().endsWith(".md")) {
            throw new IllegalArgumentException("必须上传 .md 文件");
        }
        // 2. 读取文件内容
        String markdown = null;
        try {
            markdown = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.error("文件读取失败");
        }
        // 3. 转换为 HTML
        String html = markdownService.renderToHtml(markdown);
        return Result.success(html);
    }

}
