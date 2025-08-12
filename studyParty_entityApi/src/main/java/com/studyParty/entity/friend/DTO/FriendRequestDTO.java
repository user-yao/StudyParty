package com.studyParty.entity.friend.DTO;

import com.studyParty.entity.friend.FriendRequest;
import com.studyParty.entity.user.DTO.UserDTO;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class FriendRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private FriendRequest friendRequest;
    private UserDTO user;
}
