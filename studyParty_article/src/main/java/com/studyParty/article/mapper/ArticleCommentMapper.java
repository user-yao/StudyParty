package com.studyParty.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studyParty.entity.article.ArticleComment;
import com.studyParty.entity.article.DTO.ArticleCommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
    IPage<ArticleCommentDTO> selectArticleCommentWithUser(IPage<ArticleCommentDTO> page, @Param("articleId") Long articleId, @Param("userId") Long userId);
}
