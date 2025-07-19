package com.studyparty.group.domian;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.studyparty.group.common.Level;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@TableName("`group`")
public class Group {
    @TableId(type = IdType.AUTO)
    private int id;
    private int leader;
    private String groupName;
    private int groupLevel;
    private int experience;
    private int needExperience;
    private int peopleNum;
    private int maxPeopleNum;
    private String slogan;
    private String rule;
    private String head;
    private Date createTime;
    private int canJoin;


    public Group(int leader, String groupName,String slogan, String rule,Date createTime, int canJoin) {
        this.leader = leader;
        this.groupName = groupName;
        this.groupLevel = 1;
        this.experience = 0;
        this.needExperience = Level.LEVEL_1.getNeedExperience();;
        this.peopleNum = 1;
        this.maxPeopleNum = Level.LEVEL_1.getMaxPeopleNum();
        this.slogan = slogan;
        this.rule = rule;
        this.createTime = Date.valueOf(LocalDate.now());
        this.canJoin = canJoin;
    }
}
