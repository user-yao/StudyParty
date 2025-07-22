package com.studyParty.entity.group;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TableName("group_user")
public class GroupUser {
    @TableId(type = IdType.AUTO)
    private int id;
    private int groupId;
    private int userId;
    private Date addTime;

    public GroupUser(int groupId, int userId) {
        this.groupId = groupId;
        this.userId = userId;
        this.addTime = Date.valueOf(LocalDate.now());
    }
}
