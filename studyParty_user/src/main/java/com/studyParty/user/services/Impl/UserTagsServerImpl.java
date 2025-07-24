package com.studyParty.user.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.user.UserTags;
import com.studyParty.user.mapper.UserTagsMapper;
import com.studyParty.user.services.UserTagsServer;
import org.springframework.stereotype.Service;

@Service
public class UserTagsServerImpl extends ServiceImpl<UserTagsMapper, UserTags> implements UserTagsServer {
}
