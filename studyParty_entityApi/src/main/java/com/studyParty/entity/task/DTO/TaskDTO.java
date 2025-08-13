package com.studyParty.entity.task.DTO;

import com.studyParty.entity.task.Task;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.User;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TaskDTO {
    private Long id;
    private Long uploader;
    private String title;
    private String context;
    private Integer isOver;
    private Long isTrueId;
    private Long starCoin;
    private Long starPrestige;
    private Timestamp createTime;
    private Integer status;
    private String name;
    private String head;
    private String school;
}
