package com.studyParty.entity.group.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class GroupJoinDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long groupId;
    private Long groupLeader;
    private Long userId;
    private String groupName;
    private String leaderName;
    private String username;
    private String context;
    private int isInvited;//0:普通加入 1:邀请加入
    private int isPass;//0:待审核 1:通过 2:未通过
    private Timestamp joinTime;

    public GroupJoinDTO(Long groupId, Long userId, String context) {
        this.groupId = groupId;
        this.userId = userId;
        this.context = context;
        this.isPass = 0;
        this.joinTime = Timestamp.valueOf(LocalDateTime.now());
    }

    public GroupJoinDTO(Long groupId, Long groupLeader, Long userId, String context, int isInvited) {
        this.groupId = groupId;
        this.groupLeader = groupLeader;
        this.userId = userId;
        this.context = context;
        this.isInvited = isInvited;
        this.isPass = 0;
        this.joinTime = Timestamp.valueOf(LocalDateTime.now());
    }
}