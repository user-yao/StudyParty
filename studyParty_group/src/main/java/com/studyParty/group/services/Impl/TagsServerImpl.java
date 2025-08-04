package com.studyParty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.Tags;
import com.studyParty.group.mapper.TagsMapper;
import com.studyParty.group.services.TagsServer;
import org.springframework.stereotype.Service;

@Service

public class TagsServerImpl extends ServiceImpl<TagsMapper, Tags> implements TagsServer {
}
