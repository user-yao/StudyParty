package com.studyParty.article.controller;

import com.studyParty.article.common.Result;
import com.studyParty.article.mapper.ArticleUserMapper;
import com.studyParty.entity.article.DTO.ArticleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articleUser")
@RequiredArgsConstructor
public class ArticleUserController {
    private final ArticleUserMapper articleUserMapper;
    @PostMapping("/myCollectArticle")
    public Result<?> myCollectArticle(@RequestHeader("X-User-Id") String userId){
        return Result.success(articleUserMapper.myCollectArticle(Long.parseLong(userId)));
    }

}
