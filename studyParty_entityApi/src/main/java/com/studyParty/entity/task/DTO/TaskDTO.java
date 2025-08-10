package com.studyParty.entity.task.DTO;

import com.studyParty.entity.task.Task;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TaskDTO {
    private Task task;
    private UserDTO user;
}
