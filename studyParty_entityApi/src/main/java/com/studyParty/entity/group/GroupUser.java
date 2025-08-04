package com.studyParty.entity.group;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TableName("`group_user`")
public class GroupUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long groupId;
    private Long userId;
    private int contribution;
    private Date addTime;
    /// 新加群员专用
    public GroupUser(Long groupId, Long userId) {
        this.groupId = groupId;
        this.userId = userId;
        this.contribution = 0;
        this.addTime = Date.valueOf(LocalDate.now());
    }
}
