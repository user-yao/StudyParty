package com.studyParty.entity.user.DTO;

import com.studyParty.entity.user.User;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private User user;
    private String token;
}
