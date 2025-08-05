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
import com.studyParty.entity.task.Task;
import com.studyParty.entity.task.TaskAnswer;
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
                             String title,
                             @RequestParam("file") MultipartFile markdown,
                             @RequestParam("file") MultipartFile[] sources,
                             @RequestHeader("X-User-Id") String userId){
        TaskAnswer taskAnswer = new TaskAnswer();
        String processedMarkdown = markdownService.checkMarkdown(markdown);
        if(processedMarkdown == null){
            return Result.error("上传文件错误");
        }
        Map<String, Source> sourceMap = new HashMap<>();
        for (MultipartFile source : sources) {
            Source source1 = sourceServer.getSourceUrl(source); // 返回如: /uploads/2025/03/15/cat.jpg
            sourceMap.put(source.getOriginalFilename(), source1);
        }
        // 替换 Markdown 中的文件引用
        processedMarkdown = markdownService.updateMarkdown(markdown, sourceMap, processedMarkdown);
        taskAnswer.setTaskId(taskId);
        taskAnswer.setAnswerer(Long.parseLong(userId));
        taskAnswer.setContext(processedMarkdown);
        taskAnswer.setCreateTime(new Timestamp(System.currentTimeMillis()));
        taskAnswer.setNice(0L);
        User user = businessServer.selectUserById(Long.parseLong(userId));
        taskAnswer.setStatus(user.getStatus());
        taskAnswerMapper.insert(taskAnswer);
        for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
            Source source = entry.getValue();
            source.setTaskAnswerId(taskAnswer.getId());
            sourceMapper.insert(source);
        }
        return Result.success();
    }
    @PostMapping("/deleteTaskAnswer")
    public Result<?> deleteTask(Long taskId, @RequestHeader("X-User-Id") String userId){
        Task task = taskMapper.selectById(taskId);
        if(task == null){
            return Result.error("任务不存在");
        }
        if(!task.getUploader().equals(Long.parseLong(userId))){
            return Result.error("没有权限");
        }
        if(!sourceServer.deleteTaskSource(taskId,true)){
            return Result.error("删除文件失败");
        }
        taskMapper.deleteById(taskId);
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
        return Result.success();
    }
    @PostMapping("/TaskAnswerList")
    public Result<?> TaskAnswerList(Long taskId,
                                    @RequestParam(defaultValue = "1") int currentPage){
        Page<TaskAnswer> page = new Page<>(currentPage,10);
        QueryWrapper<TaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        return Result.success(taskAnswerMapper.selectPage(page, queryWrapper));
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
        QueryWrapper<TaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id",taskId);
        queryWrapper.eq("is_true",1);
        return Result.success(taskAnswerMapper.selectOne(queryWrapper));
    }
}
