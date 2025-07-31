package com.studyparty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.entity.Source;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.entity.user.User;
import com.studyparty.group.common.Result;
import com.studyparty.group.mapper.GroupMapper;
import com.studyparty.group.mapper.GroupTaskMapper;
import com.studyparty.group.mapper.GroupUserMapper;
import com.studyparty.group.mapper.SourceMapper;
import com.studyparty.group.services.MarkdownService;
import com.studyparty.group.services.SourceServer;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@RestController("/groupTask")
public class GroupTaskController {
    @Autowired
    private  GroupTaskMapper groupTaskMapper;
    @Autowired
    private SourceServer sourceServer;
    @Autowired
    private MarkdownService markdownService;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private SourceMapper sourceMapper;
    @Autowired
    private GroupUserMapper groupUserMapper;

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
    @DeleteMapping("/{id}")
    public Result<?> deletePost(@PathVariable Long id) {
        groupTaskMapper.deleteById(id);
        sourceServer.deleteSource(id);
        return Result.success();
    }

    @PostMapping("/upload-markdown")
    public Result<?> uploadMarkdownFile(@RequestParam("file") MultipartFile file,
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
        // 1. 检查文件类型
        if (!"text/markdown".equals(file.getContentType()) &&
                !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".md")) {
            return Result.error("文件类型错误");
        }
        // 2. 读取文件内容
        String processedMarkdown = null;
        try {
            processedMarkdown = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.error("文件读取失败");
        }

        Map<String, Source> sourceMap = new HashMap<>();
        for (MultipartFile source : sources) {
            Source source1 = sourceServer.getSourceUrl(source); // 返回如: /uploads/2025/03/15/cat.jpg
            sourceMap.put(source.getOriginalFilename(), source1);
        }
        // 替换 Markdown 中的文件引用
        for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
            String originalFilename = entry.getKey();
            String serverUrl = entry.getValue().getUrl();

            // 处理标准 Markdown 图片语法: ![](filename.jpg)
            String imagePattern = "!\\$$(.*?)\\$\\$" + Pattern.quote(originalFilename) + "\\$";
            processedMarkdown = processedMarkdown.replaceAll(imagePattern, "![$1](" + serverUrl + ")");

            // 处理普通链接语法: [](filename.jpg)
            String linkPattern = "\\$$(.*?)\\$\\$" + Pattern.quote(originalFilename) + "\\$";
            processedMarkdown = processedMarkdown.replaceAll(linkPattern, "[$1](" + serverUrl + ")");

            // 处理 HTML 音频标签: <audio src="filename.mp3">
            String audioPattern = "<audio(.*?)src=(\"|')" + Pattern.quote(originalFilename) + "(\"|')(.*?)>";
            processedMarkdown = processedMarkdown.replaceAll(audioPattern, "<audio$1src=\"" + serverUrl + "\"$4>");

            // 处理 HTML 视频标签: <video src="filename.mp4">
            String videoPattern = "<video(.*?)src=(\"|')" + Pattern.quote(originalFilename) + "(\"|')(.*?)>";
            processedMarkdown = processedMarkdown.replaceAll(videoPattern, "<video$1src=\"" + serverUrl + "\"$4>");
        }
        GroupTask groupTask = new GroupTask();
        groupTask.setGroupId(groupId);
        groupTask.setGroupTask(processedMarkdown);
        groupTask.setGroupTaskUploader(userId);
        groupTask.setGroupTaskStartTime(startTime);
        groupTask.setGroupTaskLastTime(deadline);
        groupTask.setGroupTaskFinish(0L);
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("groupId", groupId);
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
