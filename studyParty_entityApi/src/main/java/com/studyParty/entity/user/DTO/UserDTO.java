package com.studyParty.entity.user.DTO;

import lombok.*;

import java.sql.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int id;
    private String name;
    private String head;
    private String sex;
    private String major;
    private String grade;
    private int status;
    private int starPrestige;
    private String phone;
    private String school;
    private int clockIn;
    private String email;
    private Date lastLogin;
    private Date createDate;
    private Integer finishTask;
}
