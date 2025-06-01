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
}