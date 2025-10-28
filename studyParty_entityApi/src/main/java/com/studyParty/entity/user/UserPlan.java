package com.studyParty.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@TableName("user_plan")
public class UserPlan implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String planContext;
    private Timestamp startTime;
    private Timestamp endTime;
    private int isStart;
    private int isEnd;

    public UserPlan(String planContext, Timestamp startTime, Long userId) {
        this.planContext = planContext;
        this.startTime = startTime;
        this.endTime = null;
        this.userId = userId;
        this.isStart = 0;
        this.isEnd = 0;
    }
}