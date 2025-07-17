package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyparty.group.domian.GroupJoin;
import com.studyparty.group.mapper.GroupJoinMapper;
import com.studyparty.group.services.GroupJoinServer;
import org.springframework.stereotype.Service;

@Service
public class GroupJoinServerImpl extends ServiceImpl<GroupJoinMapper, GroupJoin> implements GroupJoinServer {
}
