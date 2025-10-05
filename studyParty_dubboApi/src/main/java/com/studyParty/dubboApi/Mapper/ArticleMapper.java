package com.studyParty.dubboApi.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studyParty.entity.article.Article;
import com.studyParty.entity.article.DTO.ArticleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    List<ArticleDTO> selectUserArticle(Long userId);
}
