package com.studyParty.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@TableName("user_tags")
public class UserTags {
    @TableId(type = IdType.AUTO)
    private int id;
    private int userId;
    private int tagId;
}
