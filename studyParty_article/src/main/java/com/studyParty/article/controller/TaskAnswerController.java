package com.studyParty.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.article.common.Result;
import com.studyParty.article.mapper.ArticleMapper;
import com.studyParty.article.mapper.SourceMapper;
import com.studyParty.article.mapper.TaskAnswerMapper;
import com.studyParty.article.mapper.TaskMapper;
import com.studyParty.article.services.MarkdownService;
import com.studyParty.article.services.SourceServer;
import com.studyParty.article.services.TaskAnswerServer;
import com.studyParty.article.services.TaskServer;
import com.studyParty.dubboApi.services.BusinessServer;
import com.studyParty.entity.Source;
import com.studyParty.entity.task.DTO.TaskAnswerDTO;
import com.studyParty.entity.task.Task;
import com.studyParty.entity.task.TaskAnswer;
import com.studyParty.entity.user.User;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("/taskAnswer")
@RequiredArgsConstructor
public class TaskAnswerController {
    private final TaskMapper taskMapper;
    private final TaskServer taskServer;
    private final ArticleMapper articleMapper;
    private final SourceMapper sourceMapper;
    private final BusinessServer businessServer;
    private final MarkdownService markdownService;
    private final SourceServer sourceServer;
    private final TaskAnswerMapper taskAnswerMapper;
    private final TaskAnswerServer taskAnswerServer;


    @PostMapping("/addTaskAnswer")
    public Result<?> addTask(Long taskId,
                             String markdown,
                             @Parameter(description = "资源文件数组", schema = @Schema(type = "array", implementation = MultipartFile.class))
                             @RequestPart(value = "sources", required = false) MultipartFile[] sources,
                             @RequestHeader("X-User-Id") String userId){
        TaskAnswer taskAnswer = new TaskAnswer();
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
        taskAnswer.setTaskId(taskId);
        taskAnswer.setAnswerer(Long.parseLong(userId));
        taskAnswer.setContext(processedMarkdown);
        taskAnswer.setCreateTime(new Timestamp(System.currentTimeMillis()));
        taskAnswer.setNice(0L);
        User user = businessServer.selectUserById(Long.parseLong(userId));
        taskAnswer.setStatus(user.getStatus());
        taskAnswerMapper.insert(taskAnswer);
        if (!sourceMap.isEmpty()) {
            for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
                Source source = entry.getValue();
                source.setTaskAnswerId(taskAnswer.getId());
                sourceMapper.insert(source);
            }
        }

        return Result.success();
    }
    @PostMapping("/deleteTaskAnswer")
    public Result<?> deleteTaskAnswer(Long taskAnswerId, @RequestHeader("X-User-Id") String userId){
        TaskAnswer taskAnswer = taskAnswerMapper.selectById(taskAnswerId);
        if(taskAnswer == null){
            return Result.error("任务不存在");
        }
        if(!taskAnswer.getAnswerer().equals(Long.parseLong(userId))){
            return Result.error("没有权限");
        }
        if(!sourceServer.deleteTaskSource(taskAnswerId,true)){
            return Result.error("删除文件失败");
        }
        taskAnswerMapper.deleteById(taskAnswerId);
        return Result.success();
    }
    @PostMapping("trueTaskAnswer")
    public Result<?> trueTask(Long taskAnswerId, @RequestHeader("X-User-Id") String userId){
        TaskAnswer taskAnswer = taskAnswerMapper.selectById(taskAnswerId);
        if(taskAnswer == null){
            return Result.error("答案不存在");
        }
        Task task = taskMapper.selectById(taskAnswer.getTaskId());
        if(task.getUploader() != Long.parseLong(userId)){
            return Result.error("没有权限");
        }
        if(!taskAnswerServer.trueTask(taskAnswer)){
            return Result.error("任务不存在");
        }
        businessServer.addStarPrestige(task.getUploader(),task.getStarPrestige());
        businessServer.addUserTask(task.getUploader(),1,taskAnswerId);
        return Result.success();
    }
    @PostMapping("/TaskAnswerList")
    public Result<?> TaskAnswerList(Long taskId,
                                    @RequestParam(defaultValue = "1") int currentPage){
        Page<TaskAnswerDTO> page = new Page<>(currentPage,10);

        return Result.success(taskAnswerMapper.selectTaskAnswerWithUser(page, taskId));
    }
    @PostMapping("/selectTaskAnswer")
    public Result<?> selectTaskAnswer(Long taskId){
        Task task = taskMapper.selectById(taskId);
        if (task == null){
            return Result.error("任务不存在");
        }
        if(task.getIsOver() == 0){
            return Result.error("任务未结束");
        }
        return Result.success(taskAnswerMapper.selectTaskTrueAnswer(taskId));
    }
}
