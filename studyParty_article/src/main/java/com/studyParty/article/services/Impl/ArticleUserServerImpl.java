package com.studyParty.article.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.article.mapper.ArticleUserMapper;
import com.studyParty.article.services.ArticleUserServer;
import com.studyParty.entity.article.ArticleUser;
import org.springframework.stereotype.Service;

@Service
public class ArticleUserServerImpl extends ServiceImpl<ArticleUserMapper, ArticleUser> implements ArticleUserServer {
}
