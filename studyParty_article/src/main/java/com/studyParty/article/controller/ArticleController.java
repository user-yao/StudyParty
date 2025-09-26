package com.studyParty.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.article.common.Result;
import com.studyParty.article.mapper.ArticleMapper;
import com.studyParty.article.mapper.ArticleUserMapper;
import com.studyParty.article.mapper.SourceMapper;
import com.studyParty.article.services.MarkdownService;
import com.studyParty.article.services.SourceServer;
import com.studyParty.dubboApi.services.BusinessServer;
import com.studyParty.entity.Source;
import com.studyParty.entity.article.Article;
import com.studyParty.entity.article.ArticleUser;
import com.studyParty.entity.article.DTO.ArticleDTO;
import com.studyParty.entity.user.User;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController("article")
public class ArticleController {
    private final ArticleMapper articleMapper;
    private final SourceMapper sourceMapper;
    private final MarkdownService markdownService;
    private final SourceServer sourceServer;
    private final ArticleUserMapper articleUserMapper;
    @DubboReference
    private BusinessServer businessServer;
    @PostMapping("/myArticle")
    public Result<?> myArticle(@RequestHeader("X-User-Id") String userId) {
        return Result.success(articleMapper.myArticle(Long.parseLong(userId)));
    }
    @PostMapping("/searchArticle")
    public Result<?> searchArticle(String searchContext, int currentPage, @RequestHeader("X-User-Id") String userId) {
        if (currentPage <= 0) {
            currentPage = 1;
        }
        Page<ArticleDTO> page = new Page<>(currentPage, 10);
        return Result.success(articleMapper.selectArticle(page, searchContext, Long.parseLong(userId)));
    }
    @PostMapping("/createArticle")
    public Result<?> createArticle(String title,
                                   String summary,
                                   String markdown,
                                   @Parameter(description = "资源文件数组", schema = @Schema(type = "array", implementation = MultipartFile.class))
                                   @RequestPart(value = "sources", required = false) MultipartFile[] sources,
                                   @RequestHeader("X-User-Id") String userId) {
        String processedMarkdown = markdown;
        if(processedMarkdown == null){
            return Result.error("上传文件错误");
        }
        Map<String, Source> sourceMap = new HashMap<>();
        // 只有当sources不为null且不为空数组时才处理资源文件
        if (sources != null && sources.length > 0) {
            for (MultipartFile source : sources) {
                Source source1 = sourceServer.getSourceUrl(source); // 返回如: /uploads/2025/03/15/cat.jpg
                sourceMap.put(source.getOriginalFilename(), source1);
            }
            // 替换 Markdown 中的文件引用
            processedMarkdown = markdownService.updateMarkdown(sourceMap, processedMarkdown);
        }
        // 替换 Markdown 中的文件引用
        processedMarkdown = markdownService.updateMarkdown(sourceMap, processedMarkdown);
        User user = businessServer.selectUserById(Long.parseLong(userId));
        Article article = new Article(title,summary,processedMarkdown,Long.parseLong(userId),user.getStatus());
        articleMapper.insert(article);
        ArticleUser articleUser = new ArticleUser(article.getId(), Long.parseLong(userId));
        articleUserMapper.insert(articleUser);
        if (!sourceMap.isEmpty()) {
            for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
                Source source = entry.getValue();
                source.setArticleId(article.getId());
                sourceMapper.insert(source);
            }
        }
        return Result.success();
    }
    @PostMapping("/deleteArticle")
    public Result<?> deleteArticle(Long articleId, @RequestHeader("X-User-Id") String userId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null){
            return Result.error("文章不存在");
        }
        if (!article.getUploader().equals(Long.parseLong(userId))){
            return Result.error("没有权限");
        }
        articleMapper.deleteById(articleId);
        articleUserMapper.delete(new QueryWrapper<ArticleUser>().eq("article_id",articleId));
        sourceServer.deleteSource(articleId,false);
        return Result.success();
    }

    @GetMapping("/recommend")
    public Result<?> recommendArticles(@RequestParam(defaultValue = "1") int page, @RequestHeader("X-User-Id") String userId) {
        Page<ArticleDTO> page1 = new Page<>(page, 10);
        return Result.success(articleMapper.recommendArticles(page1, Long.parseLong(userId)));
    }
    @PostMapping("/articleById")
    public Result<?> articleById(Long articleId,@RequestHeader("X-User-Id") String userId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null){
            return Result.error("文章不存在");
        }
        User user = businessServer.selectUserById(article.getUploader());
        QueryWrapper<ArticleUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId)
                .eq("user_id", Long.parseLong(userId));
        ArticleUser articleUser = articleUserMapper.selectOne(queryWrapper);
        if (articleUser == null){
            articleUser = new ArticleUser();
            articleUser.setArticleId(articleId);
            articleUser.setUserId(Long.parseLong(userId));
            articleUser.setIsNice(0);
            articleUser.setIsCollect(0);
            articleUser.setIsView(1);
            articleUserMapper.insert(articleUser);
        }else if (articleUser.getIsView() == 0){
            articleUser.setIsView(1);
            articleUserMapper.updateById(articleUser);
        }
        ArticleDTO articleRecommendArticlesDTO =
                new ArticleDTO(article, user,articleUser);
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);
        return Result.success(articleRecommendArticlesDTO);
    }
    @PostMapping("/niceArticle")
    public Result<?> niceArticle(Long articleId, @RequestHeader("X-User-Id") String userId) {
        QueryWrapper<ArticleUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId)
                .eq("user_id", Long.parseLong(userId));
        ArticleUser articleUser = articleUserMapper.selectOne(queryWrapper);
        Article article = articleMapper.selectById(articleId);
        if (articleUser == null){
            articleUser = new ArticleUser();
            articleUser.setArticleId(articleId);
            articleUser.setUserId(Long.parseLong(userId));
            articleUser.setIsNice(1);
            articleUser.setIsCollect(0);
            articleUser.setIsView(0);
            article.setNice(article.getNice() + 1);
            articleUserMapper.insert(articleUser);
            articleMapper.updateById(article);
            return Result.success("点赞成功");
        }else {
            if (articleUser.getIsNice() == 1){
                articleUser.setIsNice(0);
                article.setNice(article.getNice() - 1);
                articleUserMapper.updateById(articleUser);
                articleMapper.updateById(article);
                return Result.success("取消点赞");
            }else if (articleUser.getIsNice() == 0){
                articleUser.setIsNice(1);
                article.setNice(article.getNice() + 1);
                articleUserMapper.updateById(articleUser);
                articleMapper.updateById(article);
                return Result.success("点赞成功");
            }
        }
        return Result.error("操作失败");
    }
    @PostMapping("/collectArticle")
    public Result<?> collectArticle(Long articleId, @RequestHeader("X-User-Id") String userId) {
         QueryWrapper<ArticleUser> queryWrapper = new QueryWrapper<>();
         queryWrapper.eq("article_id", articleId)
                 .eq("user_id", Long.parseLong(userId));
         ArticleUser articleUser = articleUserMapper.selectOne(queryWrapper);
         Article article = articleMapper.selectById(articleId);
         if (articleUser == null){
             articleUser = new ArticleUser();
             articleUser.setArticleId(articleId);
             articleUser.setUserId(Long.parseLong(userId));
             articleUser.setIsNice(0);
             articleUser.setIsCollect(1);
             articleUser.setIsView(0);
             article.setCollect(article.getCollect() + 1);
             articleUserMapper.insert(articleUser);
             articleMapper.updateById(article);
             return Result.success("收藏成功");
         } else {
             if (articleUser.getIsCollect() == 1){
                 articleUser.setIsCollect(0);
                 article.setCollect(article.getCollect() - 1);
                 articleUserMapper.updateById(articleUser);
                 articleMapper.updateById(article);
                 return Result.success("取消收藏");
             }else if (articleUser.getIsCollect() == 0){
                 articleUser.setIsCollect(1);
                 article.setCollect(article.getCollect() + 1);
                 articleUserMapper.updateById(articleUser);
                 articleMapper.updateById(article);
                 return Result.success("收藏成功");
             }
         }
         return Result.error("操作失败");
    }
}