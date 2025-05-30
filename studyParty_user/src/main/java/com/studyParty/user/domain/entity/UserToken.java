package com.studyParty.user.domain.entity;

import com.studyParty.user.domain.User;
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
