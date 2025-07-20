package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyparty.group.domian.GroupUser;
import com.studyparty.group.mapper.GroupUserMapper;
import com.studyparty.group.services.GroupUserServer;
import org.springframework.stereotype.Service;

@Service

public class GroupUserServerImpl extends ServiceImpl<GroupUserMapper, GroupUser> implements GroupUserServer {
}
