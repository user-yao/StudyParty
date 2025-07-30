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
@TableName("group_task")
public class GroupTask {
    @TableId(type = IdType.AUTO)
    private int id;
    private int groupId;
    private String groupTask;
    private String groupTaskUploader;
    private Timestamp groupTaskStartTime;
    private Timestamp groupTaskLastTime;
    private String groupTaskFinish;
    private String groupTaskContext;
    private String groupTaskUnfinished;
}
