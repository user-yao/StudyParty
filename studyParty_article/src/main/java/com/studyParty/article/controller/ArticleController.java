package com.studyParty.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.article.common.Result;
import com.studyParty.article.mapper.ArticleMapper;
import com.studyParty.article.mapper.SourceMapper;
import com.studyParty.article.services.MarkdownService;
import com.studyParty.article.services.SourceServer;
import com.studyParty.dubboApi.services.BusinessServer;
import com.studyParty.entity.Source;
import com.studyParty.entity.article.Article;
import com.studyParty.entity.article.DTO.ArticleDTO;
import com.studyParty.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController("article")
public class ArticleController {
    private final ArticleMapper articleMapper;
    private final SourceMapper sourceMapper;
    private final MarkdownService markdownService;
    private final SourceServer sourceServer;
    @DubboReference
    private BusinessServer businessServer;
    @PostMapping("/myArticle")
    public Result<?> myArticle(@RequestHeader("X-User-Id") String userId) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uploader", userId);
        queryWrapper.select("id", "uploader",  "title", "summary", "nice", "collect", "view_cont", "comment_cont", "create_time", "is_featured", "status");
        return Result.success(articleMapper.selectList(queryWrapper));
    }
    @PostMapping("/articleById")
    public Result<?> articleById(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null){
            return Result.error("文章不存在");
        }
        article.setViewCont(article.getViewCont() + 1);
        articleMapper.updateById(article);
        return Result.success(article);
    }
    @PostMapping("/searchArticle")
    public Result<?> searchArticle(String searchContext, int currentPage) {
        if (currentPage <= 0) {
            currentPage = 1;
        }
        Page<ArticleDTO> page = new Page<>(currentPage, 10);
        return Result.success(articleMapper.selectArticleWithUser(page, searchContext));
    }
    @PostMapping("/createArticle")
    public Result<?> createArticle(String title,
                                   String summary,
                                   @RequestParam("file") MultipartFile markdown,
                                   @RequestParam("file") MultipartFile[] sources,
                                   @RequestHeader("X-User-Id") String userId) {
        Article article = new Article();
        String processedMarkdown = markdownService.checkMarkdown(markdown);
        if(processedMarkdown == null){
            return Result.error("上传文件错误");
        }
        Map<String, Source> sourceMap = new HashMap<>();
        for (MultipartFile source : sources) {
            Source source1 = sourceServer.getSourceUrl(source); // 返回如: /uploads/2025/03/15/cat.jpg
            sourceMap.put(source.getOriginalFilename(), source1);
        }
        // 替换 Markdown 中的文件引用
        processedMarkdown = markdownService.updateMarkdown(markdown, sourceMap, processedMarkdown);
        article.setTitle(title);
        article.setSummary(summary);
        article.setContent(processedMarkdown);
        article.setUploader(Long.parseLong(userId));
        article.setNice(0L);
        article.setCollect(0L);
        article.setViewCont(0L);
        article.setCommentCont(0L);
        article.setCreateTime(String.valueOf(System.currentTimeMillis()));
        User user = businessServer.selectUserById(Long.parseLong(userId));
        article.setStatus(user.getStatus());
        articleMapper.insert(article);
        for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
            Source source = entry.getValue();
            source.setArticleId(article.getId());
            sourceMapper.insert(source);
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
        sourceServer.deleteSource(articleId,false);
        return Result.success();
    }
}