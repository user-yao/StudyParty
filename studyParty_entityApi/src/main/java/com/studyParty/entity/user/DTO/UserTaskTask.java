package com.studyParty.entity.user.DTO;

import com.studyParty.entity.task.Task;
import com.studyParty.entity.task.TaskAnswer;
import com.studyParty.entity.user.UserTask;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskTask implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Task task;
    private TaskAnswer taskAnswer;
}
