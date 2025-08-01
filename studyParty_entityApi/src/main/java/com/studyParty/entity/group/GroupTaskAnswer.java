package com.studyParty.entity.group;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TableName("group_task_answer")
public class GroupTaskAnswer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long groupTaskId;
    private Long userId;
    private String context;
    private Timestamp time;
    private int haveSource;
    private int score;

}
