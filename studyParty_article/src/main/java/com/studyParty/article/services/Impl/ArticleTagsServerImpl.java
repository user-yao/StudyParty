package com.studyParty.article.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.article.mapper.ArticleTagsMapper;
import com.studyParty.article.services.ArticleTagsServer;
import com.studyParty.entity.article.ArticleTags;
import org.springframework.stereotype.Service;

@Service
public class ArticleTagsServerImpl extends ServiceImpl<ArticleTagsMapper, ArticleTags> implements ArticleTagsServer {
}
