package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyparty.group.domian.GroupTask;
import com.studyparty.group.mapper.GroupTaskMapper;
import com.studyparty.group.services.GroupTaskServer;
import io.swagger.v3.oas.annotations.servers.Server;

@Server
public class GroupTaskServerImpl extends ServiceImpl<GroupTaskMapper, GroupTask> implements GroupTaskServer {
}
