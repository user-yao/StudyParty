package com.studyparty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.entity.Source;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.entity.group.GroupUser;
import com.studyparty.group.common.Result;
import com.studyparty.group.mapper.*;
import com.studyparty.group.services.MarkdownService;
import com.studyparty.group.services.SourceServer;
import lombok.RequiredArgsConstructor;
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
@RestController("/groupTask")
@RequiredArgsConstructor
public class GroupTaskController {
    private final GroupTaskMapper groupTaskMapper;
    private final SourceServer sourceServer;
    private final MarkdownService markdownService;
    private final GroupMapper groupMapper;
    private final SourceMapper sourceMapper;
    private final GroupUserMapper groupUserMapper;
    private final GroupTaskAnswerMapper groupTaskAnswerMapper;

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
        queryWrapper.eq("group_id", groupId);
        return Result.success(groupTaskMapper.selectPage(page, queryWrapper));
    }
    @DeleteMapping("deleteGroupTask/{id}")
    public Result<?> deleteGroupTask(@PathVariable Long id,@RequestHeader("X-User-Id") String userId) {
        if(groupMapper.selectById(userId).getLeader() != Integer.parseInt(userId) ||
                groupMapper.selectById(userId).getDeputy() != Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
        QueryWrapper<GroupTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_task_id", id);
        groupTaskAnswerMapper.delete(queryWrapper);
        if(groupTaskMapper.deleteById(id) == 0){
            return Result.error("任务不存在");
        }
        sourceServer.deleteSource(id,false);
        return Result.success();
    }

    @PostMapping("/upload-markdown")
    public Result<?> uploadMarkdownFile(@RequestParam("file") MultipartFile markdown,
                                        @RequestParam("file") MultipartFile[] sources,
                                        Long groupId,
                                        Timestamp deadline,
                                        Timestamp startTime,
                                        @RequestHeader("X-User-Id") String userId){
        Group group = groupMapper.selectById(groupId);
        if(group.getLeader() != Integer.parseInt(userId)){
             return Result.error("权限错误");
        }
        if(group.getDeputy() != Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
        if(group.getTeacher() != Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
        if(group.getEnterprise() != Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
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
        GroupTask groupTask = new GroupTask();
        groupTask.setGroupId(groupId);
        groupTask.setGroupTask(processedMarkdown);
        groupTask.setGroupTaskUploader(userId);
        groupTask.setGroupTaskStartTime(startTime);
        groupTask.setGroupTaskLastTime(deadline);
        groupTask.setGroupTaskFinish(0L);
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        int predecessor = 0;
        if (group.getTeacher() != 0){
            predecessor++;
        }
        if (group.getEnterprise() != 0){
            predecessor++;
        }
        groupTask.setGroupTaskUnfinished(groupUserMapper.selectCount(queryWrapper) - predecessor);
        groupTaskMapper.insert(groupTask);
        for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
            Source source = entry.getValue();
            source.setGroupTaskId(groupTask.getId());
            sourceMapper.insert(source);
        }
        return Result.success();
    }

}
