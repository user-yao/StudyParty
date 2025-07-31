package com.studyParty.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TableName("image")
@Data
public class Source {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String image;
    private String url;
    private String filePath;
    private Long articleId;
    private Long groupTaskId;
    private Long achievementId;
    private Long groupTaskAnswerId;

    public Source(String image, String url, String filePath, Long groupTaskId) {
        this.image = image;
        this.url = url;
        this.filePath = filePath;
        this.groupTaskId = groupTaskId;
    }
}
