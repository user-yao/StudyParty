package com.studyParty.entity.user.DTO;

import com.studyParty.entity.task.Task;
import com.studyParty.entity.task.TaskAnswer;
import com.studyParty.entity.user.UserTask;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskTask {
    private Task task;
    private TaskAnswer taskAnswer;
}
