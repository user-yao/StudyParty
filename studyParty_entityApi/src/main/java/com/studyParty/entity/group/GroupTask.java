package com.studyParty.entity.group;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TableName("`group_task`")
public class GroupTask implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long groupId;
    private String groupTask;
    private String groupTaskUploader;
    private Timestamp groupTaskStartTime;
    private Timestamp groupTaskLastTime;
    private Long groupTaskFinish;
    private String groupTaskContext;
    private Long groupTaskUnfinished;
}
