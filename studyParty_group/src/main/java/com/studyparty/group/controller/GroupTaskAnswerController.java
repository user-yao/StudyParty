package com.studyparty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.studyParty.entity.Source;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.entity.user.User;
import com.studyparty.group.common.Result;
import com.studyparty.group.mapper.GroupMapper;
import com.studyparty.group.mapper.GroupTaskAnswerMapper;
import com.studyparty.group.mapper.GroupTaskMapper;
import com.studyparty.group.mapper.SourceMapper;
import com.studyparty.group.services.MarkdownService;
import com.studyparty.group.services.SourceServer;
import com.studyparty.studyparty_dubboapi.services.BusinessServer;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/***
 * url:功能说明
 * /submit:提交作业
 * /score:评分
 * /getGroupTaskAnswers:获取作业列表
 * /getGroupTaskAnswer:获取我的作业
 */
@RestController("/groupTaskAnswer")
@RequiredArgsConstructor
public class GroupTaskAnswerController {
    private final GroupTaskMapper groupTaskMapper;
    private final SourceMapper sourceMapper;
    private final SourceServer sourceServer;
    private final GroupTaskAnswerMapper groupTaskAnswerMapper;
    private final MarkdownService markdownService;
    private final GroupMapper groupMapper;
    @DubboReference
    private BusinessServer businessServer;
    @PostMapping("/submit")
    public Result<?> submit(Long groupTaskId,
                            @RequestParam("file") MultipartFile markdown,
                            @RequestParam("file") MultipartFile[] sources,
                            @RequestHeader("X-User-Id") String userId){
        GroupTask groupTask = groupTaskMapper.selectById(groupTaskId);
        if (groupTask == null) {
            return Result.error("任务不存在");
        }
        QueryWrapper<GroupTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_task_id", groupTaskId);
        queryWrapper.eq("user_id", userId);
        GroupTaskAnswer isSubmitted = groupTaskAnswerMapper.selectOne(queryWrapper);
        if (isSubmitted != null) {
            sourceServer.deleteSource(isSubmitted.getGroupTaskId(),true);
            groupTaskMapper.deleteById(isSubmitted.getId());
        }
        GroupTaskAnswer groupTaskAnswer = new GroupTaskAnswer();

        // 1. 检查文件类型
        String processedMarkdown = markdownService.checkMarkdown(markdown);
        if (processedMarkdown == null) {
            return Result.error("上传文件类型错误");
        }
        Map<String, Source> sourceMap = new HashMap<>();
        if(sources.length == 0){
            groupTaskAnswer.setHaveSource(0);
            groupTaskAnswer.setContext(processedMarkdown);
        }else{
            groupTaskAnswer.setHaveSource(1);
            for (MultipartFile source : sources) {
                Source source1 = sourceServer.getSourceUrl(source);
                sourceMap.put(source.getOriginalFilename(), source1);
            }
            processedMarkdown = markdownService.updateMarkdown(markdown, sourceMap, processedMarkdown);
            groupTaskAnswer.setContext(processedMarkdown);
        }
        groupTaskAnswer.setGroupTaskId(groupTaskId);
        groupTaskAnswer.setUserId(Long.valueOf(userId));
        groupTaskAnswer.setTime(new Timestamp(System.currentTimeMillis()));
        groupTaskAnswer.setScore(-1);
        groupTaskAnswerMapper.insert(groupTaskAnswer);
        if (sources.length != 0){
            for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
                Source source = entry.getValue();
                source.setGroupTaskAnswerId(groupTaskAnswer.getId());
                sourceMapper.insert(source);
            }
        }
        return Result.success();
    }
    @PostMapping("/score")
    public Result<?> score(Long groupTaskAnswerId, int score, @RequestHeader("X-User-Id") String userId){
        GroupTaskAnswer groupTaskAnswer = groupTaskAnswerMapper.selectById(groupTaskAnswerId);
        GroupTask groupTask = groupTaskMapper.selectById(groupTaskAnswer.getGroupTaskId());
        Group group = groupMapper.selectById(groupTask.getGroupId());
        if(!Objects.equals(group.getLeader(), Long.valueOf(userId)) || !Objects.equals(group.getDeputy(), Long.valueOf(userId))){
            return Result.error("权限错误");
        }
        groupTaskAnswer.setScore(score);
        groupTaskAnswerMapper.updateById(groupTaskAnswer);
        return Result.success();
    }
    @PostMapping("/getGroupTaskAnswers")
    public Result<?> getGroupTaskAnswers(Long groupTaskId, @RequestHeader("X-User-Id") String userId){
        GroupTask groupTask = groupTaskMapper.selectById(groupTaskId);
        Group group = groupMapper.selectById(groupTask.getGroupId());
        if(!Objects.equals(group.getLeader(), Long.valueOf(userId))
                || !Objects.equals(group.getDeputy(), Long.valueOf(userId))
                || !Objects.equals(group.getTeacher(), Long.valueOf(userId))
                || !Objects.equals(group.getEnterprise(), Long.valueOf(userId))){
            return Result.error("权限错误");
        }
        QueryWrapper<GroupTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_task_id", groupTaskId);
        List<GroupTaskAnswer> groupTaskAnswers = groupTaskAnswerMapper.selectList(queryWrapper);
        return Result.success(businessServer.selectUser(groupTask.getGroupId(),Long.valueOf(userId)));
    }
    @PostMapping("/getMyGroupTaskAnswers")
    public Result<?> getMyGroupTaskAnswers(Long groupTaskId, @RequestHeader("X-User-Id") String userId){
        GroupTask groupTask = groupTaskMapper.selectById(groupTaskId);
        QueryWrapper<GroupTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_task_id", groupTaskId);
        queryWrapper.eq("user_id", userId);
        return Result.success(groupTaskAnswerMapper.selectOne(queryWrapper));
    }
}
