package com.studyParty.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.article.common.Result;
import com.studyParty.article.mapper.SourceMapper;
import com.studyParty.article.mapper.TaskMapper;
import com.studyParty.article.services.MarkdownService;
import com.studyParty.article.services.SourceServer;
import com.studyParty.dubboApi.services.BusinessServer;
import com.studyParty.entity.Source;
import com.studyParty.entity.task.DTO.TaskDTO;
import com.studyParty.entity.task.Task;
import com.studyParty.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskMapper taskMapper;
    private final SourceMapper sourceMapper;
    private final BusinessServer businessServer;
    private final MarkdownService markdownService;
    private final SourceServer sourceServer;


    @PostMapping("/addTask")
    public Result<?> addTask(String title,
                             String markdown,
                             @RequestParam("file") MultipartFile[] sources,
                             @RequestHeader("X-User-Id") String userId){
        Task task = new Task();
        String processedMarkdown = markdown;
        if(processedMarkdown == null){
            return Result.error("上传文件错误");
        }
        Map<String, Source> sourceMap = new HashMap<>();
        // 只有当sources不为null且不为空数组时才处理资源文件
        if (sources != null && sources.length > 0) {
            for (MultipartFile source : sources) {
                Source source1 = sourceServer.getSourceUrl(source); // 返回如: /uploads/2025/03/15/cat.jpg
                sourceMap.put(source.getOriginalFilename(), source1);
            }
            // 替换 Markdown 中的文件引用
            processedMarkdown = markdownService.updateMarkdown(sourceMap, processedMarkdown);
        }
        // 替换 Markdown 中的文件引用
        processedMarkdown = markdownService.updateMarkdown(sourceMap, processedMarkdown);
        task.setTitle(title);
        task.setContext(processedMarkdown);
        task.setUploader(Long.parseLong(userId));
        task.setCreateTime(new Timestamp(System.currentTimeMillis()));
        task.setStarPrestige(10L);
        User user = businessServer.selectUserById(Long.parseLong(userId));
        task.setStatus(user.getStatus());
        taskMapper.insert(task);
        if (!sourceMap.isEmpty()) {
            for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
                Source source = entry.getValue();
                source.setTaskId(task.getId());
                sourceMapper.insert(source);
            }
        }
        return Result.success();
    }
    @PostMapping("/deleteTask")
    public Result<?> deleteTask(Long taskId, @RequestHeader("X-User-Id") String userId){
        Task task = taskMapper.selectById(taskId);
        if(task == null){
            return Result.error("任务不存在");
        }
        if(!task.getUploader().equals(Long.parseLong(userId))){
            return Result.error("没有权限");
        }
        if(!sourceServer.deleteTaskSource(taskId,false)){
            return Result.error("删除文件失败");
        }
        taskMapper.deleteById(taskId);
        return Result.success();
    }

    @PostMapping("/searchTask")
    public Result<?> searchTask(String searchContent,
                                      @RequestParam(defaultValue = "1") int currentPage) {
        // 创建分页对象
        Page<TaskDTO> page = new Page<>(currentPage, 10);
        return Result.success(taskMapper.selectTaskWithUser(page, searchContent));
    }
}
