package com.studyParty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.group.GroupTags;
import com.studyParty.group.mapper.GroupTagsMapper;
import com.studyParty.group.services.GroupTagsServer;
import org.springframework.stereotype.Service;

@Service

public class GroupTagsServerImpl extends ServiceImpl<GroupTagsMapper, GroupTags> implements GroupTagsServer {
}
