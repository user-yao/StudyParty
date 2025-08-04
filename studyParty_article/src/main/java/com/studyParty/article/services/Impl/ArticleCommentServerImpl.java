package com.studyParty.article.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.article.mapper.ArticleCommentMapper;
import com.studyParty.article.services.ArticleCommentServer;
import com.studyParty.entity.article.ArticleComment;
import org.springframework.stereotype.Service;

@Service
public class ArticleCommentServerImpl extends ServiceImpl<ArticleCommentMapper, ArticleComment> implements ArticleCommentServer {
}
