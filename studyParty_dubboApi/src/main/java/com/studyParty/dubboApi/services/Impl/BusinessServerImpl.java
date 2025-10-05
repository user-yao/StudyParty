package com.studyParty.dubboApi.services.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.studyParty.dubboApi.Mapper.*;
import com.studyParty.entity.article.DTO.ArticleDTO;
import com.studyParty.entity.group.DTO.GroupUserDTO;
import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.entity.task.Task;
import com.studyParty.entity.task.TaskAnswer;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.DTO.UserTaskGroup;
import com.studyParty.entity.user.DTO.UserTaskTask;
import com.studyParty.entity.user.User;
import com.studyParty.dubboApi.services.BusinessServer;
import com.studyParty.entity.user.UserArticle;
import com.studyParty.entity.user.UserTask;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@DubboService
@RequiredArgsConstructor
public class BusinessServerImpl implements BusinessServer {
    private final UserMapper userMapper;
    private final UserTaskMapper userTaskMapper;
    private final TaskMapper taskMapper;
    private final TaskAnswerMapper taskAnswerMapper;
    private final GroupTaskAnswerMapper groupTaskAnswerMapper;
    private final GroupTaskMapper groupTaskMapper;
    private final GroupUserMapper groupUserMapper;
    private final UserArticleMapper userArticleMapper;
    private final ArticleMapper articleMapper;

    @Override
    @DubboService
    public List<GroupUserDTO> selectUser(Long groupId, Long userId) {
        return userMapper.selectGroupUser(groupId, userId);
    }
    @DubboService
    @Override
    public User selectUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @DubboService
    public void addStarPrestige(Long userId, Long starPrestige) {
        User user = userMapper.selectById(userId);
        UpdateWrapper<User> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("id", userId);
        queryWrapper.set("star_prestige", user.getStarPrestige()+starPrestige);
        userMapper.update(queryWrapper);
    }

    @Override
    @DubboService
    public void addUserTask(Long userId, int taskType, Long taskId) {
        QueryWrapper<UserTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("task_id", taskId);
        if (userTaskMapper.selectCount(queryWrapper) > 0) {
            return;
        }
        UserTask userTask = new UserTask();
        userTask.setUserId(userId);
        userTask.setTaskId(taskId);
        userTask.setTaskType(taskType);
        userTaskMapper.insert(userTask);
    }

    @Override
    @DubboService
    public void addUserArticle(Long userId, Long articleId) {
        QueryWrapper<UserArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("article_id", articleId);
        if (userArticleMapper.selectCount(queryWrapper) > 0) {
            return;
        }
        UserArticle userArticle = new UserArticle();
        userArticle.setUserId(userId);
        userArticle.setArticleId(articleId);
        userArticleMapper.insert(userArticle);
    }

    @Override
    public List<ArticleDTO> selectUserArticle(Long userId) {
        List<ArticleDTO> userArticles = articleMapper.selectUserArticle(userId);
        return userArticles;
    }

    @Override
    public List<UserTaskTask> selectUserTaskTask(Long userId) {
        List<UserTaskTask> userTaskTasks = new ArrayList<>();
        List<TaskAnswer> taskAnswers = taskAnswerMapper.selectTrueTaskAnswer(userId);
        for (TaskAnswer taskAnswer : taskAnswers) {
            QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_true_id", taskAnswer.getId());
            Task task = taskMapper.selectOne(queryWrapper);
            userTaskTasks.add(new UserTaskTask(task, taskAnswer));
        }
        return userTaskTasks;
    }

    @Override
    public List<UserTaskGroup> selectUserTaskGroup(Long userId) {
        List<UserTaskGroup> userTaskGroups = new ArrayList<>();
        QueryWrapper<GroupTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<GroupTaskAnswer> groupTaskAnswerList = groupTaskAnswerMapper.selectList(queryWrapper);
        for (GroupTaskAnswer groupTaskAnswer : groupTaskAnswerList) {
            QueryWrapper<GroupTask> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("id", groupTaskAnswer.getGroupTaskId());
            GroupTask groupTask = groupTaskMapper.selectOne(queryWrapper1);
            userTaskGroups.add(new UserTaskGroup(groupTask, groupTaskAnswer));
        }
        return userTaskGroups;
    }

    @Override
    public List<Long> selectGroupUser(Long groupId) {
        QueryWrapper<GroupUser> wrapper = new QueryWrapper<>();
        wrapper.select("group_user");
        wrapper.eq("group_id", groupId);
        return groupUserMapper.selectObjs( wrapper);
    }


}
