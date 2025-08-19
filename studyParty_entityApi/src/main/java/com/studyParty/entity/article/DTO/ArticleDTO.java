package com.studyParty.entity.article.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ArticleDTO {
    private Long id;
    private Long uploader;
    private String title;
    private String summary;
    private String content;
    private Long nice;
    private Long collect;
    private Long viewCont;
    private Long commentCont;
    private String createTime;
    private int isFeatured;
    private int status;
    private String name;
    private String head;
    private String school;
}
