package com.studyParty.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studyParty.entity.article.Article;
import com.studyParty.entity.article.DTO.ArticleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    IPage<ArticleDTO> selectArticle(IPage<ArticleDTO> page, @Param("searchContext") String searchContext, Long userId);
    IPage<ArticleDTO> recommendArticles(IPage<ArticleDTO> page, Long userId);
    List<ArticleDTO> myArticle(Long userId);
}
