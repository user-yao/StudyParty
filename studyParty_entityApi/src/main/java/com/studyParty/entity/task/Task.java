package com.studyParty.entity.task;

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
@TableName("`task`")
public class Task implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long uploader;
    private String title;
    private String context;
    private Integer isOver;
    private Long isTrueId;
    private Long starCoin;
    private Long starPrestige;
    private Timestamp createTime;
    private Integer status;
}
