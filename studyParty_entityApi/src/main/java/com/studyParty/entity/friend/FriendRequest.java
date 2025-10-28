package com.studyParty.entity.friend;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@TableName("friend_request")
public class FriendRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long friendId;
    private String context;
    private Timestamp createTime;
    private int isConsent;

    public FriendRequest(Long userId, Long friendId, String context) {
        this.userId = userId;
        this.friendId = friendId;
        this.context = context;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.isConsent = 0;
    }
}
