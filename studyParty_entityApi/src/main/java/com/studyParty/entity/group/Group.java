package com.studyParty.entity.group;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private Long id;
    private Long leader;
    private Long deputy;
    private Date deputyTime;
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
    private Long teacher;
    private Long enterprise;

    /// 创建小组专用
    public Group(Long leader, String groupName,String slogan, String rule,Date createTime, int canJoin) {
        this.leader = leader;
        this.deputy = leader;
        this.deputyTime = Date.valueOf(LocalDate.now());
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
