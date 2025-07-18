package com.studyparty.group.domian;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
    private int suffer;
    private int peopleNum;
    private int maxPeopleNum;
    private String slogan;
    private String rule;

    public Group(int leader, String groupName, int groupLevel, int suffer, int peopleNum, int maxPeopleNum, String slogan, String rule) {
        this.leader = leader;
        this.groupName = groupName;
        this.groupLevel = groupLevel;
        this.suffer = suffer;
        this.peopleNum = peopleNum;
        this.maxPeopleNum = maxPeopleNum;
        this.slogan = slogan;
        this.rule = rule;
    }
}
