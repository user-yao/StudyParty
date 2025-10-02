package com.studyParty.entity.article.DTO;

import com.studyParty.entity.article.Article;
import com.studyParty.entity.article.ArticleUser;
import com.studyParty.entity.user.User;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ArticleDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long uploader;
    private String title;
    private String summary;
    private String content;
    private Long nice;
    private Long collect;
    private Long viewCount;
    private Long commentCount;
    private Timestamp createTime;
    private int isFeatured;
    private int status;
    private String name;
    private String head;
    private String school;
    private Long starPrestige;
    private int isNice;
    private int isCollect;
    private int isView;
    private int isUploader;

    public ArticleDTO(Article article, User user, ArticleUser articleUser) {
        this.id = article.getId();
        this.uploader = article.getUploader();
        this.title = article.getTitle();
        this.summary = article.getSummary();
        this.content = article.getContent();
        this.nice = article.getNice();
        this.collect = article.getCollect();
        this.viewCount = article.getViewCount();
        this.commentCount = article.getCommentCount();
        this.createTime = article.getCreateTime();
        this.isFeatured = article.getIsFeatured();
        this.status = article.getStatus();
        this.name = user.getName();
        this.head = user.getHead();
        this.school = user.getSchool();
        this.starPrestige = user.getStarPrestige();
        this.isNice = articleUser.getIsNice();
        this.isCollect = articleUser.getIsCollect();
        this.isView = articleUser.getIsView();
    }
}
