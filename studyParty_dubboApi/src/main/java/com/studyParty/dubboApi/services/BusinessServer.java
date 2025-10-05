package com.studyParty.dubboApi.services;

import com.studyParty.entity.article.DTO.ArticleDTO;
import com.studyParty.entity.group.DTO.GroupUserDTO;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.DTO.UserTaskGroup;
import com.studyParty.entity.user.DTO.UserTaskTask;
import com.studyParty.entity.user.User;
import com.studyParty.entity.user.UserArticle;
import com.studyParty.entity.user.UserTask;

import java.util.List;

public interface BusinessServer {
    List<GroupUserDTO> selectUser(Long groupId, Long userId);
    User selectUserById(Long userId);
    void addStarPrestige(Long userId, Long starPrestige);
    void addUserTask(Long userId, int taskType, Long taskId);
    void addUserArticle(Long userId, Long articleId);
    List<UserTaskTask> selectUserTaskTask(Long userId);
    List<UserTaskGroup> selectUserTaskGroup(Long userId);
    List<Long> selectGroupUser(Long groupId);
    List<ArticleDTO> selectUserArticle(Long userId);
}
