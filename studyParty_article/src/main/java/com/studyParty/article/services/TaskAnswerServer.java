package com.studyParty.article.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.studyParty.entity.task.TaskAnswer;

public interface TaskAnswerServer extends IService<TaskAnswer> {
    boolean trueTask(TaskAnswer taskAnswerId);
}
