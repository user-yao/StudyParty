package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyparty.group.domian.GroupTags;
import com.studyparty.group.mapper.GroupTagsMapper;
import com.studyparty.group.services.GroupTagsServer;
import io.swagger.v3.oas.annotations.servers.Server;

@Server
public class GroupTagsServerImpl extends ServiceImpl<GroupTagsMapper, GroupTags> implements GroupTagsServer {
}
