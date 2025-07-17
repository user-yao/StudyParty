package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyparty.group.domian.Tags;
import com.studyparty.group.mapper.TagsMapper;
import com.studyparty.group.services.TagsServer;
import io.swagger.v3.oas.annotations.servers.Server;

@Server
public class TagsServerImpl extends ServiceImpl<TagsMapper, Tags> implements TagsServer {
}
