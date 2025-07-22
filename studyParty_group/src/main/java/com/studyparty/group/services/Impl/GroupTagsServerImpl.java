package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.group.GroupTags;
import com.studyparty.group.mapper.GroupTagsMapper;
import com.studyparty.group.services.GroupTagsServer;
import org.springframework.stereotype.Service;

@Service

public class GroupTagsServerImpl extends ServiceImpl<GroupTagsMapper, GroupTags> implements GroupTagsServer {
}
