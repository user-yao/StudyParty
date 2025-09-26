package com.studyParty.entity.article;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@TableName("`article`")
public class Article implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
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

    public Article(String title, String summary, String content,Long uploader,int status) {
        this.uploader = uploader;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.nice = 0L;
        this.collect = 0L;
        this.viewCount = 0L;
        this.commentCount = 0L;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.isFeatured = 0;
        this.status = status;
    }
}
