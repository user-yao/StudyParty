package com.studyParty.entity.user.DTO;

import com.studyParty.entity.user.User;
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

    public UserDTO(String remark, User  user) {
        this.remark = remark;
        this.id = user.getId();
        this.name = user.getName();
        this.head = user.getHead();
        this.sex = user.getSex();
        this.major = user.getMajor();
        this.grade = user.getGrade();
        this.status = user.getStatus();
        this.starPrestige = user.getStarPrestige();
        this.phone = user.getPhone();
        this.school = user.getSchool();
        this.clockIn = user.getClockIn();
        this.email = user.getEmail();
        this.lastLogin = user.getLastLogin();
        this.createDate = user.getCreateDate();
        this.finishTask = user.getFinishTask();
    }
}
