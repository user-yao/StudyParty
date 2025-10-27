package com.studyParty.entity.article;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@TableName(" article_user ")
public class ArticleUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Long userId;
    private int isNice;
    private int isCollect;
    private int isView;
    private int isUploader;

    public ArticleUser(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
        this.isNice = 0;
        this.isCollect = 0;
        this.isView = 0;
        this.isUploader = 1;
    }
}
