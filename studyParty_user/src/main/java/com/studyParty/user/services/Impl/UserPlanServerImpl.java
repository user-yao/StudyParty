package com.studyParty.user.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.user.UserPlan;
import com.studyParty.user.mapper.UserPlanMapper;
import com.studyParty.user.services.UserPlanServer;
import org.springframework.stereotype.Service;

@Service
public class UserPlanServerImpl extends ServiceImpl<UserPlanMapper, UserPlan> implements UserPlanServer {
}
