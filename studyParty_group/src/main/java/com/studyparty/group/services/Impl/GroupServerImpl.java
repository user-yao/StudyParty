package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyparty.group.domian.Group;
import com.studyparty.group.mapper.GroupMapper;
import com.studyparty.group.services.GroupServer;
import org.springframework.stereotype.Service;

@Service
public class GroupServerImpl extends ServiceImpl<GroupMapper, Group> implements GroupServer {
}
