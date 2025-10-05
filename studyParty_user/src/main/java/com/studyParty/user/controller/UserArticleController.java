package com.studyParty.user.controller;

import com.studyParty.dubboApi.services.BusinessServer;
import com.studyParty.entity.article.DTO.ArticleDTO;
import com.studyParty.entity.user.DTO.UserTaskGroup;
import com.studyParty.entity.user.DTO.UserTaskTask;
import com.studyParty.user.common.Result;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/userArticle")
@RequiredArgsConstructor
public class UserArticleController {
    @DubboReference
    private BusinessServer businessServer;
    @GetMapping("/selectMyUserArticle")
    public Result<?> selectMyUserArticle(@RequestHeader("X-User-Id") String userId){
        List<ArticleDTO> userArticle = businessServer.selectUserArticle(Long.parseLong(userId));
        return Result.success(userArticle);
    }
}
