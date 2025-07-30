package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.Image;
import com.studyParty.entity.group.DTO.GroupTaskDTO;
import com.studyParty.entity.group.GroupTask;
import com.studyparty.group.Utils.BusinessException;
import com.studyparty.group.mapper.GroupTaskMapper;
import com.studyparty.group.mapper.ImageMapper;
import com.studyparty.group.services.GroupTaskServer;
import com.studyparty.group.services.ImageServer;
import com.studyparty.group.services.MarkdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GroupTaskServerImpl extends ServiceImpl<GroupTaskMapper, GroupTask> implements GroupTaskServer {
    private final ImageServer imageServer;
    private final ImageMapper imageMapper;
    private final MarkdownService markdownService;
    private final GroupTaskMapper groupTaskMapper;

    @Transactional
    public int createPost(GroupTask groupTask) {
        // 简单验证
        if (groupTask.getGroupTask() == null || groupTask.getGroupTask().trim().isEmpty()) {
            throw new BusinessException("任务标题不能为空");
        }

        if (groupTask.getGroupTaskContext() == null || groupTask.getGroupTaskContext().trim().isEmpty()) {
            throw new BusinessException("任务内容不能为空");
        }
        if(groupTask.getGroupTaskStartTime() == null){
            throw new BusinessException("任务开始时间不能为空");
        }
        if(groupTask.getGroupTaskLastTime() == null){
            throw new BusinessException("任务结束时间不能为空");
        }
        if (groupTask.getGroupTaskLastTime().compareTo(groupTask.getGroupTaskStartTime()) < 0) {
            throw new BusinessException("任务结束时间不能早于开始时间");
        }
        return groupTaskMapper.insert(groupTask);
    }
    @Transactional(readOnly = true)
    public GroupTaskDTO getGroupTaskDTO(Long id) {
        GroupTask groupTask = groupTaskMapper.selectById(id);
        if (groupTask == null) {
            throw new BusinessException("任务不存在");
        }
        // 将Markdown转换为HTML
        String htmlContent = markdownService.renderToHtml(groupTask.getGroupTaskContext());
        QueryWrapper<Image> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_task_id", id);
        // 查询帖子图片
        List<Image> images = imageMapper.selectList(queryWrapper);
        // 构建DTO返回
        return new GroupTaskDTO(
                groupTask,
                htmlContent,
                images
        );
    }
    @Transactional
    public void deletePost(Long id) {
        // 查询帖子是否存在
        if (groupTaskMapper.selectById(id) == null) {
            throw new BusinessException("帖子不存在");
        }

        imageServer.deleteImage(id);
        // 删除帖子
        groupTaskMapper.deleteById(id);
    }
}
