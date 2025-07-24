package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.user.Tags;
import com.studyparty.group.mapper.TagsMapper;
import com.studyparty.group.services.TagsServer;
import org.springframework.stereotype.Service;

@Service

public class TagsServerImpl extends ServiceImpl<TagsMapper, Tags> implements TagsServer {
}
