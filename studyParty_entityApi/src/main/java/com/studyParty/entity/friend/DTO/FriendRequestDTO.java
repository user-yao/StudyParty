package com.studyParty.entity.friend.DTO;


import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class FriendRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long friendId;
    private Timestamp createTime;
    private String context;
    private int isConsent;
    private String name;
    private String head;
    private String status;
    private String school;
}
