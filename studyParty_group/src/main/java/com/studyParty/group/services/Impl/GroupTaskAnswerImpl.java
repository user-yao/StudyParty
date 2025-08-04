package com.studyParty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.group.mapper.GroupTaskAnswerMapper;
import com.studyParty.group.services.GroupTaskAnswerServer;
import org.springframework.stereotype.Service;


@Service
public class GroupTaskAnswerImpl extends ServiceImpl<GroupTaskAnswerMapper, GroupTaskAnswer> implements GroupTaskAnswerServer {
}
