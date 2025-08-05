package com.studyParty.article.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.article.mapper.TaskAnswerMapper;
import com.studyParty.article.mapper.TaskMapper;
import com.studyParty.article.services.TaskAnswerServer;
import com.studyParty.entity.task.Task;
import com.studyParty.entity.task.TaskAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskAnswerServerImpl extends ServiceImpl<TaskAnswerMapper, TaskAnswer> implements TaskAnswerServer {
    @Autowired
    private TaskAnswerMapper taskAnswerMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Override
    public boolean trueTask(TaskAnswer taskAnswer) {
        Task task = taskMapper.selectById(taskAnswer.getTaskId());
        if(task == null){
            return false;
        }
        task.setIsOver(1);
        task.setIsTrueId(taskAnswer.getId());
        taskAnswer.setIsTrue(1);
        taskMapper.updateById(task);
        taskAnswerMapper.updateById(taskAnswer);
        return true;
    }
}
