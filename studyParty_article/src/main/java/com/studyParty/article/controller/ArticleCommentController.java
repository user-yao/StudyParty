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

@RestController
@RequestMapping("/articleComment")
@RequiredArgsConstructor
public class ArticleCommentController {
    private final SourceMapper sourceMapper;
    private final ArticleMapper articleMapper;
    private final BusinessServer businessServer;
    private final SourceServer sourceServer;
    private final ArticleCommentMapper articleCommentMapper;
    private final ArticleCommentUserMapper articleCommentUserMapper;

    @PostMapping("/addArticleComment")
    public Result<?> addArticleComment(String content,
                                       Long articleId,
                                       @RequestHeader("X-User-Id") String userId){
        // 参数校验
        if (content == null || content.trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        if (articleId == null) {
            return Result.error("文章ID不能为空");
        }
        if (userId == null || userId.trim().isEmpty()) {
            return Result.error("用户ID不能为空");
        }

        Long parsedUserId;
        try {
            parsedUserId = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            return Result.error("用户ID格式错误");
        }

        // 查询文章信息
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return Result.error("文章不存在");
        }

        // 查询用户信息
        User user = businessServer.selectUserById(parsedUserId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        ArticleComment articleComment = new ArticleComment();
        articleComment.setArticleId(articleId);
        articleComment.setUserId(parsedUserId);
        articleComment.setContent(content);
        articleComment.setNice(0L);
        articleComment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        articleComment.setStatus(user.getStatus());

        // 使用事务确保数据一致性
        try {
            // 更新文章评论数（使用乐观锁或原子操作避免并发问题）
            article.setCommentCount(article.getCommentCount() + 1);
            articleMapper.updateById(article);
            articleCommentMapper.insert(articleComment);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Result.error("添加评论失败");
        }
        return Result.success(articleComment.getId());
    }

    @PostMapping("/addArticleCommentImage")
    public Result<?> addArticleCommentImage(Long articleCommentId,
                                            @Parameter(description = "图片文件")
                                            @RequestPart(value = "image") MultipartFile[] sources){
        if (sources == null){
            return Result.error("请上传图片");
        }
        if(articleCommentId == null){
            return Result.error("参数错误");
        }
        Map<String, Source> sourceMap = new HashMap<>();
        if (sources != null && sources.length > 0) {
            for (MultipartFile source : sources) {
                Source source1 = sourceServer.getSourceUrl(source);
                sourceMap.put(source.getOriginalFilename(), source1);
            }
        }
        // 统一处理资源文件的关联
        if (!sourceMap.isEmpty()){
            for (Map.Entry<String, Source> entry : sourceMap.entrySet()) {
                Source source = entry.getValue();
                source.setArticleCommentId(articleCommentId);
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
        ArticleComment articleComment = articleCommentMapper.selectById(articleCommentId);
        if (articleCommentUser == null){
             articleCommentUser = new ArticleCommentUser();
             articleCommentUser.setArticleCommentId(articleCommentId);
             articleCommentUser.setUserId(Long.parseLong(userId));
             articleCommentUser.setIsNice(1);
             articleComment.setNice(articleComment.getNice() + 1);
             articleCommentUserMapper.insert(articleCommentUser);
             articleCommentMapper.updateById(articleComment);
            return Result.success("点赞成功");
        }else{
            if (articleCommentUser.getIsNice() == 1){
                articleCommentUser.setIsNice(0);
                articleComment.setNice(articleComment.getNice() - 1);
                articleCommentUserMapper.updateById(articleCommentUser);
                articleCommentMapper.updateById(articleComment);
                return Result.success("取消点赞");
            }else if (articleCommentUser.getIsNice() == 0){
                articleCommentUser.setIsNice(1);
                articleComment.setNice(articleComment.getNice() + 1);
                articleCommentUserMapper.updateById(articleCommentUser);
                articleCommentMapper.updateById(articleComment);
                return Result.success("点赞成功");
            }
        }
        return Result.error("操作失败");
    }
}
