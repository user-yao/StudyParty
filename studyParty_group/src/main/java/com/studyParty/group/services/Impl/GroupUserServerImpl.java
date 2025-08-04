package com.studyParty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.group.mapper.GroupUserMapper;
import com.studyParty.group.services.GroupUserServer;
import org.springframework.stereotype.Service;

@Service

public class GroupUserServerImpl extends ServiceImpl<GroupUserMapper, GroupUser> implements GroupUserServer {
}
