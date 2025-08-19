package com.studyParty.entity.article.DTO;

import lombok.*;

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
}
