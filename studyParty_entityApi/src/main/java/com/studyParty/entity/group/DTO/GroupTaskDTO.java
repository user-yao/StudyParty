package com.studyParty.entity.group.DTO;

import com.studyParty.entity.Source;
import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.User;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class GroupTaskDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private GroupTask groupTask;
    private List<Source> sources;
    private UserDTO user;
}
