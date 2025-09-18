package com.studyParty.user.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.user.UserCreateTask;
import com.studyParty.entity.user.UserTask;
import com.studyParty.user.mapper.UserCreateTaskMapper;
import com.studyParty.user.mapper.UserTaskMapper;
import com.studyParty.user.services.UserCreateTaskServer;
import com.studyParty.user.services.UserTaskServer;
import org.springframework.stereotype.Service;

@Service
public class UserCreateTaskServerImpl extends ServiceImpl<UserCreateTaskMapper, UserCreateTask> implements UserCreateTaskServer {
}
