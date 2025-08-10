package com.studyParty.entity.group.DTO;

import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class GroupTaskAnswerDTO {
    private GroupTaskAnswer groupTaskAnswer;
    private UserDTO user;
}
