package com.studyParty.article.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.article.mapper.ArticleCollectMapper;
import com.studyParty.article.services.ArticleCollectServer;
import com.studyParty.entity.article.ArticleCollect;
import org.springframework.stereotype.Service;

@Service
public class ArticleCollectServerImpl extends ServiceImpl<ArticleCollectMapper, ArticleCollect> implements ArticleCollectServer {
}
