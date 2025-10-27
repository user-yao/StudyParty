package com.studyParty.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@TableName("users")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String password;
    private String head;
    private String sex;
    private String major;
    private String grade;
    private int status;
    private Long starCoin;
    private Long groupCoin;
    private Long starPrestige;
    private String phone;
    private String school;
    private int clockIn;
    private String email;
    private Date lastLogin;
    private Date createDate;
    private Long finishTask;
    private Long articleNum;
    private Long taskNum;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
    /// 注册专用
    public User(String name, String password, String sex, String major, String grade, int status, String phone, String school, String email) {
        this.name = name;
        this.password = password;
        this.sex = sex;
        this.major = major;
        this.grade = grade;
        this.status = status;
        this.phone = phone;
        this.school = school;
        this.email = email;
        this.starCoin  = 100L;
        this.groupCoin = 0L;
        this.starPrestige = 0L;
        this.clockIn = 1;
        this.lastLogin = Date.valueOf(LocalDate.now());
        this.finishTask = 0L;
        this.articleNum = 0L;
        this.taskNum = 0L;

    }
    /// 修改个人信息专用
    public User(String name, String sex, String major, String grade, String phone, String school, String email) {
        this.name = name;
        this.sex = sex;
        this.major = major;
        this.grade = grade;
        this.phone = phone;
        this.school = school;
        this.email = email;
    }
}