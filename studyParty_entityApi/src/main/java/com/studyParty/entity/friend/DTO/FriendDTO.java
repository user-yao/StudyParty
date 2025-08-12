package com.studyParty.entity.friend.DTO;

import com.baomidou.mybatisplus.annotation.TableName;
import com.studyParty.entity.friend.Friend;
import com.studyParty.entity.user.DTO.UserDTO;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class FriendDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Friend friend;
    private UserDTO  user;
}
