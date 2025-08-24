package com.studyParty.entity;

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
@TableName("`source`")
@Data
public class Source implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String url;
    private String filePath;
    private Long articleId;
    private Long articleCommentId;
    private Long groupTaskId;
    private Long achievementId;
    private Long groupTaskAnswerId;
    private Long taskId;
    private Long taskAnswerId;

    public Source(String name, String url, String filePath) {
        this.name = name;
        this.url = url;
        this.filePath = filePath;
    }
}
