package com.studyParty.user.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.user.UserTask;
import com.studyParty.user.mapper.UserTaskMapper;
import com.studyParty.user.services.UserTaskServer;
import org.springframework.stereotype.Service;

@Service
public class UserTaskServerImpl extends ServiceImpl<UserTaskMapper, UserTask> implements UserTaskServer {
}
