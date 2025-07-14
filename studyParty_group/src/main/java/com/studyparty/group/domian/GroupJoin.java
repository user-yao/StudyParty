package com.studyparty.group.domian;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.sql.Date;

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
    private int userId;
    private String context;
    private int isPass;
    private Date joinTime;
}
