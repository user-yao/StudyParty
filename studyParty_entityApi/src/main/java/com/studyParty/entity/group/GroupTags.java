package com.studyParty.entity.group;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TableName("group_tags")
public class GroupTags {
    @TableId(type = IdType.AUTO)
    private int id;
    private int groupId;
    private int tagId;
}
