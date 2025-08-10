package com.studyParty.group.services.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.Source;
import com.studyParty.entity.group.DTO.GroupTaskDTO;
import com.studyParty.entity.group.GroupTask;
import com.studyParty.group.mapper.GroupTaskMapper;
import com.studyParty.group.mapper.SourceMapper;
import com.studyParty.group.services.GroupTaskServer;
import com.studyParty.group.services.MarkdownService;
import com.studyParty.group.services.SourceServer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GroupTaskServerImpl extends ServiceImpl<GroupTaskMapper, GroupTask> implements GroupTaskServer {
}
