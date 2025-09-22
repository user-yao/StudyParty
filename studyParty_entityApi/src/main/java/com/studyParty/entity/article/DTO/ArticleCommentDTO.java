package com.studyParty.entity.article.DTO;

import com.studyParty.entity.Source;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ArticleCommentDTO {
    private Long id;
    private Long articleId;
    private Long userId;
    private String content;
    private Long nice;
    private String createTime;
    private int status;
    private String name;
    private String head;
    private String school;
    private Long star_prestige;
    private int isNice;
    private List<Source> sources;
}