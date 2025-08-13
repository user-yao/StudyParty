package com.studyParty.entity.task.DTO;

import com.studyParty.entity.task.TaskAnswer;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.User;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TaskAnswerDTO {
    private Long id;
    private Long taskId;
    private Long answerer;
    private String context;
    private Long nice;
    private Integer isTrue;
    private Timestamp createTime;
    private Integer status;
    private String name;
    private String head;
    private String school;
}
