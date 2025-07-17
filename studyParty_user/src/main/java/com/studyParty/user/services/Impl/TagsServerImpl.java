package com.studyParty.user.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.user.domain.Tags;
import com.studyParty.user.mapper.TagsMapper;
import com.studyParty.user.services.TagsServer;
import org.springframework.stereotype.Service;

@Service
public class TagsServerImpl extends ServiceImpl<TagsMapper, Tags> implements TagsServer {
}
