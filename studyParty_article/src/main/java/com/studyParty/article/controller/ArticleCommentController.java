package com.studyParty.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.article.common.Result;
import com.studyParty.article.mapper.ArticleCommentMapper;
import com.studyParty.article.mapper.ArticleMapper;
import com.studyParty.article.mapper.SourceMapper;
import com.studyParty.article.services.SourceServer;
import com.studyParty.dubboApi.services.BusinessServer;
import com.studyParty.entity.Source;
import com.studyParty.entity.article.Article;
import com.studyParty.entity.article.ArticleComment;
import com.studyParty.entity.article.DTO.ArticleCommentDTO;
import com.studyParty.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController("/articleComment")
@RequiredArgsConstructor
public class ArticleCommentController {
    private final ArticleMapper articleMapper;
    private final SourceMapper sourceMapper;
    private final BusinessServer businessServer;
    private final SourceServer sourceServer;
    private final ArticleCommentMapper articleCommentMapper;

    @PostMapping("/addArticleComment")
    public Result<?> addArticleComment(String content,
                                       Long articleId,
                                       @RequestParam("file") MultipartFile[] sources,
                                       @RequestHeader("X-User-Id") String userId){
        ArticleComment articleComment = new ArticleComment();
        Map<String, Source> sourceMap = new HashMap<>();
        for (MultipartFile source : sources) {
            Source source1 = sourceServer.getSourceUrl(source); // 返回如: /uploads/2025/03/15/cat.jpg
            sourceMap.put(source.getOriginalFilename(), source1);
        }
        articleComment.setArticleId(articleId);
        articleComment.setUserId(Long.parseLong(userId));
        articleComment.setContent(content);
        articleComment.setNice(0L);
        articleComment.setCreateTime(String.valueOf(System.currentTimeMillis()));
        User user = businessServer.selectUserById(Long.parseLong(userId));
        articleComment.setStatus(user.getStatus());
        articleCommentMapper.insert(articleComment);
        for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
            Source source = entry.getValue();
            source.setArticleCommentId(articleComment.getId());
            sourceMapper.insert(source);
        }
        return Result.success();
    }
    @PostMapping("/deleteArticleComment")
    public Result<?> deleteArticleComment(Long articleCommentId, @RequestHeader("X-User-Id") String userId){
        ArticleComment articleComment = articleCommentMapper.selectById(articleCommentId);
        if (articleComment == null) {
            return Result.error("文章评论不存在");
        }
        if (!articleComment.getUserId().equals(Long.parseLong(userId))) {
            return Result.error("没有权限");
        }
        articleCommentMapper.deleteById(articleCommentId);
        sourceServer.deleteSource(articleCommentId,true);
        return Result.success();
    }
    @PostMapping("/getArticleComment")
    public Result<?> getArticleComment(Long articleId, int currentPage){
        Page<ArticleCommentDTO> page = new Page<>(currentPage, 10);
        QueryWrapper<ArticleCommentDTO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        return Result.success(articleCommentMapper.selectArticleCommentWithUser(page, articleId));
    }
}
