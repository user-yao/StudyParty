package com.studyParty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.studyParty.entity.Source;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.group.common.Result;
import com.studyParty.group.mapper.GroupMapper;
import com.studyParty.group.mapper.GroupTaskAnswerMapper;
import com.studyParty.group.mapper.GroupTaskMapper;
import com.studyParty.group.mapper.SourceMapper;
import com.studyParty.group.services.Impl.GroupServerImpl;
import com.studyParty.group.services.MarkdownService;
import com.studyParty.group.services.SourceServer;
import com.studyParty.dubboApi.services.BusinessServer;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/groupTaskAnswer")
public class GroupTaskAnswerController {
    private final GroupTaskMapper groupTaskMapper;
    private final SourceMapper sourceMapper;
    private final SourceServer sourceServer;
    private final GroupTaskAnswerMapper groupTaskAnswerMapper;
    private final MarkdownService markdownService;
    private final GroupMapper groupMapper;
    private final GroupServerImpl groupServerImpl;
    @DubboReference
    private BusinessServer businessServer;
    @PostMapping("/submit")
    public Result<?> submit(Long groupTaskId,
                            String markdown,
                            @RequestHeader("X-User-Id") String userId){
        GroupTask groupTask = groupTaskMapper.selectById(groupTaskId);
        if (groupTask == null) {
            return Result.error("任务不存在");
        }
        Long userIdLong = Long.valueOf(userId);
        QueryWrapper<GroupTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_task_id", groupTaskId);
        queryWrapper.eq("user_id", userIdLong);
        GroupTaskAnswer isSubmitted = groupTaskAnswerMapper.selectOne(queryWrapper);

        GroupTaskAnswer groupTaskAnswer = new GroupTaskAnswer();
        if (markdown == null) {
            return Result.error("上传文件类型错误");
        }
        if (isSubmitted != null) {
            isSubmitted.setContext(markdown);
            isSubmitted.setTime(new Timestamp(System.currentTimeMillis()));
            groupTaskAnswerMapper.updateById(isSubmitted);
            return Result.success(isSubmitted.getId());
        }else{
            groupTaskAnswer.setGroupTaskId(groupTaskId);
            groupTaskAnswer.setUserId(userIdLong);
            groupTaskAnswer.setTime(new Timestamp(System.currentTimeMillis()));
            groupTaskAnswer.setContext(markdown);
            groupTaskAnswer.setHaveSource(0);
            groupTaskAnswer.setScore(-1);
            groupTaskAnswerMapper.insert(groupTaskAnswer);
            groupServerImpl.contributionGroup(groupTask.getGroupId(), userId);
            groupTask.setGroupTaskFinish(groupTask.getGroupTaskFinish() + 1);
            groupTaskMapper.updateById(groupTask);
            businessServer.addUserTask(userIdLong, 2, groupTaskId);
            return Result.success(groupTaskAnswer.getId());
        }
    }

    @PostMapping("/submitFile")
    public Result<?> submitFile(Long groupTaskAnswerId,
                                @RequestParam(value = "source") MultipartFile[] sources,
                                @RequestHeader("X-User-Id") String userId){
        Map<String, Source> sourceMap = new HashMap<>();
        GroupTaskAnswer groupTaskAnswer = groupTaskAnswerMapper.selectById(groupTaskAnswerId);

        if (groupTaskAnswer == null) {
            return Result.error("作业不存在");
        }

        // 处理资源文件
        if (sources != null && sources.length > 0) {
            for (MultipartFile source : sources) {
                Source source1 = sourceServer.getSourceUrl(source);
                sourceMap.put(source.getOriginalFilename(), source1);
            }

            // 更新作业标记为有附件
            groupTaskAnswer.setHaveSource(1);
            String processedMarkdown = markdownService.updateMarkdown(sourceMap, groupTaskAnswer.getContext());
            groupTaskAnswer.setContext(processedMarkdown);
            groupTaskAnswerMapper.updateById(groupTaskAnswer);

            // 插入资源记录
            for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
                Source source = entry.getValue();
                source.setGroupTaskAnswerId(groupTaskAnswerId);
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
        if(!group.getLeader().equals(Long.valueOf(userId))
                && !group.getDeputy().equals(Long.valueOf(userId))
                && !group.getTeacher().equals(Long.valueOf(userId))
                && !group.getEnterprise().equals(Long.valueOf(userId))){
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
        if(!group.getLeader().equals(Long.valueOf(userId))
                && !group.getDeputy().equals(Long.valueOf(userId))
                && !group.getTeacher().equals(Long.valueOf(userId))
                && !group.getEnterprise().equals(Long.valueOf(userId))){
            return Result.success("权限错误");
        }
        QueryWrapper<GroupTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_task_id", groupTaskId);
        return Result.success(groupTaskAnswerMapper.selectGroupTaskAnswerWithUser(groupTaskId));
    }
    @PostMapping("/getMyGroupTaskAnswers")
    public Result<?> getMyGroupTaskAnswers(Long groupTaskId, @RequestHeader("X-User-Id") String userId){
        return Result.success(groupTaskAnswerMapper.selectMyGroupTaskAnswerWithUser(groupTaskId, Long.valueOf(userId)));
    }
}
