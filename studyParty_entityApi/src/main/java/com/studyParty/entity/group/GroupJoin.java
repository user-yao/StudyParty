package com.studyParty.entity.group;

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
    private Long id;
    private Long groupId;
    private Long groupLeader;
    private Long userId;
    private String context;
    private int isPass;//1:待审核 2:通过 3:未通过
    private Timestamp joinTime;

    public GroupJoin(Long groupId, Long groupLeader, Long userId, String context, int isPass) {
        this.groupId = groupId;
        this.groupLeader = groupLeader;
        this.userId = userId;
        this.context = context;
        this.isPass = 0;
        this.joinTime = Timestamp.valueOf(LocalDateTime.now());
    }
}
