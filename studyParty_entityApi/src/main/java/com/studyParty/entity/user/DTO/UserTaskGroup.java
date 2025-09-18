package com.studyParty.entity.user.DTO;

import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.entity.user.UserTask;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private GroupTask groupTask;
    private GroupTaskAnswer groupTaskAnswer;
}
