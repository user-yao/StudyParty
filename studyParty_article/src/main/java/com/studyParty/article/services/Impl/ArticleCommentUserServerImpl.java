package com.studyParty.article.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.article.mapper.ArticleCommentUserMapper;
import com.studyParty.article.services.ArticleCommentUserServer;
import com.studyParty.entity.article.ArticleCommentUser;
import org.springframework.stereotype.Service;

@Service
public class ArticleCommentUserServerImpl extends ServiceImpl<ArticleCommentUserMapper, ArticleCommentUser> implements ArticleCommentUserServer {
}
