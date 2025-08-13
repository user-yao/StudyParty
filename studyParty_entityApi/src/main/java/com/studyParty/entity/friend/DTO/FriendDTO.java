package com.studyParty.entity.friend.DTO;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyParty.entity.friend.Friend;
import com.studyParty.entity.user.DTO.UserDTO;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class FriendDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long friendId;
    private Timestamp createTime;
    private String remark;
    private String name;
    private String head;
    private String status;
    private String school;
}
