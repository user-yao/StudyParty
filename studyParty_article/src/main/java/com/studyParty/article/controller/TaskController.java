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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskMapper taskMapper;
    private final SourceMapper sourceMapper;
    private final BusinessServer businessServer;
    private final MarkdownService markdownService;
    private final SourceServer sourceServer;
    
    @PostMapping("/createTask")
    public Result<?> createTask(String title,
                                String markdown,
                                @RequestHeader("X-User-Id") String userId){
        if (title == null || title.trim().isEmpty()) {
            return Result.error("标题不能为空");
        }
        if (markdown == null) {
            return Result.error("上传文件错误");
        }
        // 查找是否已存在相同的任务（根据标题、内容和用户ID判断）
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title)
                .eq("context", markdown)
                .eq("uploader", Long.parseLong(userId));
        Task existingTask = taskMapper.selectOne(queryWrapper);
        if (existingTask != null) {
            // 如果任务已存在，则更新任务内容和时间
            existingTask.setContext(markdown);
            existingTask.setCreateTime(new Timestamp(System.currentTimeMillis()));
            taskMapper.updateById(existingTask);
            return Result.success(existingTask.getId());
        } else {
            // 如果任务不存在，则创建新任务
            User user = businessServer.selectUserById(Long.parseLong(userId));
            Task task = new Task(Long.parseLong(userId), title, markdown,user.getStatus());
            taskMapper.insert(task);
            return Result.success(task.getId());
        }
    }
    @GetMapping("/getTaskById")
    public Result<?> getTaskById(Long taskId){
        Task task = taskMapper.selectById(taskId);
        if(task == null){
            return Result.error("任务不存在");
        }
        User user = businessServer.selectUserById(task.getUploader());
        TaskDTO taskDTO = new TaskDTO(task,user);
        return Result.success(taskDTO);
    }
    
    @PostMapping("/createTaskImage")
    public Result<?> createTaskImage(Long taskId, 
                                     @RequestParam("file") MultipartFile[] sources,
                                     @RequestHeader("X-User-Id") String userId){
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }
        if (!task.getUploader().equals(Long.parseLong(userId))) {
            return Result.error("没有权限");
        }
        
        Map<String, Source> sourceMap = new HashMap<>();
        // 只有当sources不为null且不为空数组时才处理资源文件
        if (sources != null && sources.length > 0) {
            for (MultipartFile source : sources) {
                Source source1 = sourceServer.getSourceUrl(source); // 返回如: /uploads/2025/03/15/cat.jpg
                sourceMap.put(source.getOriginalFilename(), source1);
            }
        }
        System.out.println(task.getContext());
        // 替换 Markdown 中的文件引用
        String processedMarkdown = markdownService.updateMarkdown(sourceMap, task.getContext());
        task.setContext(processedMarkdown);
        System.out.println(task.getContext());
        taskMapper.updateById(task);
        
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
    @GetMapping("/recommend")
    public Result<?> recommend(@RequestParam(defaultValue = "1") int currentPage, @RequestHeader("X-User-Id") String userId){
        // 创建分页对象
        Page<TaskDTO> page = new Page<>(currentPage, 10);
        return Result.success(taskMapper.recommendTask(page, Long.parseLong(userId)));
    }
}