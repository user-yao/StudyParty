package com.studyParty.user.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.user.UserArticle;
import com.studyParty.entity.user.UserTask;
import com.studyParty.user.mapper.UserArticleMapper;
import com.studyParty.user.mapper.UserTaskMapper;
import com.studyParty.user.services.UserArticleServer;
import com.studyParty.user.services.UserTaskServer;
import org.springframework.stereotype.Service;

@Service
public class UserArticleServerImpl extends ServiceImpl<UserArticleMapper, UserArticle> implements UserArticleServer {
}
