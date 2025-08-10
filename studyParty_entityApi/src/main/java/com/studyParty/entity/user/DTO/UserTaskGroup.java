package com.studyParty.entity.user.DTO;

import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.entity.user.UserTask;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskGroup {
    private GroupTask groupTask;
    private GroupTaskAnswer groupTaskAnswer;
}
