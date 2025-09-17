package com.studyParty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.entity.Source;
import com.studyParty.entity.group.DTO.GroupTaskDTO;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.group.common.Result;
import com.studyParty.group.mapper.*;
import com.studyParty.group.services.MarkdownService;
import com.studyParty.group.services.SourceServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/***
 * url:功能说明
 * /selectMyGroupTask:查询用户加入的群组任务
 * /deleteGroupTask:删除群组任务
 * /upload-markdown:上传群组任务
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/groupTask")
public class GroupTaskController {
    private final GroupTaskMapper groupTaskMapper;
    private final SourceServer sourceServer;
    private final MarkdownService markdownService;
    private final GroupMapper groupMapper;
    private final SourceMapper sourceMapper;
    private final GroupUserMapper groupUserMapper;
    private final GroupTaskAnswerMapper groupTaskAnswerMapper;

    @PostMapping("/selectMyGroupTask")
    public Result<?> selectMyGroupTask(Long groupId, Integer currentPage) {
        if (groupId == null) {
            return Result.error("参数错误");
        }
        if (currentPage == null || currentPage < 1) {
            currentPage = 1;
        }
        Page<GroupTaskDTO> page = new Page<>(currentPage, 10);
        return Result.success(groupTaskMapper.selectGroupTaskWithUser(page, groupId));
    }

    @PostMapping("/deleteGroupTask")
    public Result<?> deleteGroupTask(Long groupTaskId, @RequestHeader("X-User-Id") String userId) {
        if (groupMapper.selectById(userId).getLeader() != Integer.parseInt(userId) ||
                groupMapper.selectById(userId).getDeputy() != Integer.parseInt(userId)) {
            return Result.error("权限错误");
        }
        QueryWrapper<GroupTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_task_id", groupTaskId);
        groupTaskAnswerMapper.delete(queryWrapper);
        if (groupTaskMapper.deleteById(groupTaskId) == 0) {
            return Result.error("任务不存在");
        }
        sourceServer.deleteSource(groupTaskId, false);
        return Result.success();
    }

    @Operation(summary = "上传Markdown文件", description = "上传Markdown文件及相关资源文件")
    @PostMapping(value = "/uploadMarkdown", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<?> uploadMarkdownFile(
            String markdown,
            @Parameter(description = "资源文件数组", schema = @Schema(type = "array", implementation = MultipartFile.class))
            @RequestPart(value = "sources", required = false) MultipartFile[] sources,
            @Parameter(description = "任务标题")
            @RequestParam String content,
            @Parameter(description = "群组ID")
            @RequestParam Long groupId,
            @Parameter(description = "截止时间戳（毫秒）")
            @RequestParam Long deadline,
            @Parameter(description = "开始时间戳（毫秒）")
            @RequestParam Long startTime,
            @RequestHeader("X-User-Id") String userId) {
        Group group = groupMapper.selectById(groupId);
        if (group.getLeader() != Integer.parseInt(userId)) {
            return Result.error("权限错误");
        }
        
        // 修改权限检查逻辑，处理可能为null的字段
        if (group.getDeputy() != null && group.getDeputy() != Integer.parseInt(userId)) {
            return Result.error("权限错误");
        }
        
        if (group.getTeacher() != null && group.getTeacher() != Integer.parseInt(userId)) {
            return Result.error("权限错误");
        }
        
        if (group.getEnterprise() != null && group.getEnterprise() != Integer.parseInt(userId)) {
            return Result.error("权限错误");
        }
        
        // 将Long类型的时间戳转换为Timestamp类型
        Timestamp deadlineTimestamp = new Timestamp(deadline);
        Timestamp startTimeTimestamp = new Timestamp(startTime);
        
        String processedMarkdown = markdown;
        if (processedMarkdown == null) {
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
        
        GroupTask groupTask = new GroupTask();
        groupTask.setGroupId(groupId);
        groupTask.setGroupTask(content);
        groupTask.setGroupTaskContext(processedMarkdown);
        groupTask.setGroupTaskUploader(userId);
        groupTask.setGroupTaskStartTime(startTimeTimestamp);
        groupTask.setGroupTaskLastTime(deadlineTimestamp);
        groupTask.setGroupTaskFinish(0L);
        groupTask.setCreateTime(new Timestamp(System.currentTimeMillis()));
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        int predecessor = 0;
        if (group.getTeacher() != null && group.getTeacher() != 0) {
            predecessor++;
        }
        if (group.getEnterprise() != null && group.getEnterprise() != 0) {
            predecessor++;
        }
        groupTask.setGroupTaskUnfinished(groupUserMapper.selectCount(queryWrapper) - predecessor);
        groupTaskMapper.insert(groupTask);
        
        // 只有当sourceMap不为空时才处理资源文件插入
        if (!sourceMap.isEmpty()) {
            for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
                Source source = entry.getValue();
                source.setGroupTaskId(groupTask.getId());
                sourceMapper.insert(source);
            }
        }
        
        return Result.success();
    }

}
