package com.studyParty.entity.friend;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@TableName("`friend`")
public class Friend implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long friendId;
    private Timestamp createTime;
    private String remark;

    public Friend(Long userId, Long friendId) {
        this.userId = userId;
        this.friendId = friendId;
        this.createTime = new Timestamp(System.currentTimeMillis());
    }
}
