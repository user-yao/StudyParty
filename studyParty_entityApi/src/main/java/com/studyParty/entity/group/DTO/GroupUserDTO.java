package com.studyParty.entity.group.DTO;

import com.studyParty.entity.user.User;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserDTO  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String head;
    private String sex;
    private String major;
    private String grade;
    private String remark;
    private int status;
    private int starPrestige;
    private String phone;
    private String school;
    private int clockIn;
    private String email;
    private Date lastLogin;
    private Date createDate;
    private Integer finishTask;
    private boolean friend;
    private Long contribution;
    private Timestamp addTime;
}