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
public class Image {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String image;
    private String url;
    private String filePath;
    private Long articleId;
    private Long groupTaskId;
    private Long achievementId;
}
