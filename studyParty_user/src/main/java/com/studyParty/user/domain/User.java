package com.studyParty.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private int id;
    private String name;
    private String password;
    private  String head;
    private String sex;
    private String major;
    private String grade;
    private int status;
    private int starCoin;
    private int groupCoin;
    private int starPrestige;
    private String phone;
    private String school;
    private int clockIn;
    private String email;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

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
    }

    public User(String name, String password, String head, String sex, String major, String grade, int status, String phone, String school, int clockIn, String email) {
        this.name = name;
        this.password = password;
        this.head = head;
        this.sex = sex;
        this.major = major;
        this.grade = grade;
        this.status = status;
        this.phone = phone;
        this.school = school;
        this.clockIn = clockIn;
        this.email = email;
    }

}