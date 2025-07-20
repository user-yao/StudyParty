package com.studyparty.group.domian;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TableName("group_join")
public class GroupJoin {
    @TableId(type = IdType.AUTO)
    private int id;
    private int groupId;
    private int groupLeader;
    private int userId;
    private String context;
    private int isPass;//1:待审核 2:通过 3:未通过
    private Timestamp joinTime;

    public GroupJoin(int groupId, int groupLeader, int userId, String context, int isPass) {
        this.groupId = groupId;
        this.groupLeader = groupLeader;
        this.userId = userId;
        this.context = context;
        this.isPass = 0;
        this.joinTime = Timestamp.valueOf(LocalDateTime.now());
    }
}
