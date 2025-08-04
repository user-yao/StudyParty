package com.studyParty.article.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.article.mapper.ArticleMapper;
import com.studyParty.article.services.ArticleServer;
import com.studyParty.entity.article.Article;
import org.springframework.stereotype.Service;

@Service
public class ArticleServerImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleServer {
}
