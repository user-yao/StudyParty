package com.studyParty.entity.user.DTO;

import com.studyParty.entity.user.User;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {
    private User user;
    private String token;
}
