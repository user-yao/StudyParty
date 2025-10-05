package com.studyParty.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.article.ArticleUser;
import com.studyParty.entity.article.DTO.ArticleDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleUserMapper extends BaseMapper<ArticleUser> {
    List<ArticleDTO> myCollectArticle(Long userId);
}
