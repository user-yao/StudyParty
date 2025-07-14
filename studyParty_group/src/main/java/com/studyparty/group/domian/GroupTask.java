package com.studyparty.group.domian;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
    private String groupTaskLastTime;
}
