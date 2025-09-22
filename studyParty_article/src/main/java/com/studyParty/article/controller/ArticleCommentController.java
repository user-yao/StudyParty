package com.studyParty.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.article.common.Result;
import com.studyParty.article.mapper.ArticleCommentMapper;
import com.studyParty.article.mapper.ArticleCommentUserMapper;
import com.studyParty.article.mapper.ArticleMapper;
import com.studyParty.article.mapper.SourceMapper;
import com.studyParty.article.services.SourceServer;
import com.studyParty.dubboApi.services.BusinessServer;
import com.studyParty.entity.Source;
import com.studyParty.entity.article.Article;
import com.studyParty.entity.article.ArticleComment;
import com.studyParty.entity.article.ArticleCommentUser;
import com.studyParty.entity.article.DTO.ArticleCommentDTO;
import com.studyParty.entity.user.User;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestController("/articleComment")
@RequiredArgsConstructor
public class ArticleCommentController {
    private final SourceMapper sourceMapper;
    private final BusinessServer businessServer;
    private final SourceServer sourceServer;
    private final ArticleCommentMapper articleCommentMapper;
    private final ArticleCommentUserMapper articleCommentUserMapper;

    @PostMapping("/addArticleComment")
    public Result<?> addArticleComment(String content,
                                       Long articleId,
                                       @Parameter(description = "资源文件数组", schema = @Schema(type = "array", implementation = MultipartFile.class))
                                       @RequestPart(value = "sources", required = false) MultipartFile[] sources,
                                       @RequestHeader("X-User-Id") String userId){
        ArticleComment articleComment = new ArticleComment();
        Map<String, Source> sourceMap = new HashMap<>();
        if (sources != null && sources.length > 0) {
            for (MultipartFile source : sources) {
                Source source1 = sourceServer.getSourceUrl(source);
                sourceMap.put(source.getOriginalFilename(), source1);
            }
        }
        articleComment.setArticleId(articleId);
        articleComment.setUserId(Long.parseLong(userId));
        articleComment.setContent(content);
        articleComment.setNice(0L);
        articleComment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        User user = businessServer.selectUserById(Long.parseLong(userId));
        articleComment.setStatus(user.getStatus());
        articleCommentMapper.insert(articleComment);
        if (!sourceMap.isEmpty()){
            for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
                Source source = entry.getValue();
                source.setArticleCommentId(articleComment.getId());
                sourceMapper.insert(source);
            }
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
    public Result<?> getArticleComment(Long articleId, int currentPage,@RequestHeader("X-User-Id") String userId){
        Page<ArticleCommentDTO> page = new Page<>(currentPage, 10);
        return Result.success(articleCommentMapper.selectArticleCommentWithUser(page, articleId,Long.parseLong(userId)));
    }
    @PostMapping("/niceArticleComment")
    public Result<?> niceArticleComment(Long articleCommentId, @RequestHeader("X-User-Id") String userId){
        QueryWrapper<ArticleCommentUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_comment_id", articleCommentId)
                .eq("user_id", Long.parseLong(userId));
        ArticleCommentUser articleCommentUser = articleCommentUserMapper.selectOne(queryWrapper);
        if (articleCommentUser == null){
             articleCommentUser = new ArticleCommentUser();
             articleCommentUser.setArticleCommentId(articleCommentId);
             articleCommentUser.setUserId(Long.parseLong(userId));
             articleCommentUser.setIsNice(1);
             articleCommentUserMapper.insert(articleCommentUser);
            return Result.success("点赞");
        }else{
            if (articleCommentUser.getIsNice() == 1){
                articleCommentUser.setIsNice(0);
                articleCommentUserMapper.updateById(articleCommentUser);
                return Result.success("取消");
            }else if (articleCommentUser.getIsNice() == 0){
                articleCommentUser.setIsNice(1);
                articleCommentUserMapper.updateById(articleCommentUser);
                return Result.success("点赞");
            }else{
                return Result.error("操作失败");
            }
        }
    }
}
