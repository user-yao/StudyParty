package com.studyParty.entity.task.DTO;

import com.studyParty.entity.task.TaskAnswer;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TaskAnswerDTO {
    private TaskAnswer taskAnswer;
    private UserDTO user;
}
