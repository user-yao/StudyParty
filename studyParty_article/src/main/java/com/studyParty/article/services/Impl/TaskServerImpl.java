package com.studyParty.article.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.article.mapper.TaskMapper;
import com.studyParty.article.services.TaskServer;
import com.studyParty.entity.task.Task;
import com.studyParty.entity.task.TaskAnswer;
import org.springframework.stereotype.Service;

@Service
public class TaskServerImpl extends ServiceImpl<TaskMapper, Task> implements TaskServer {

}
