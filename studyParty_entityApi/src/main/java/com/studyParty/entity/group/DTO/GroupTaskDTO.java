package com.studyParty.entity.group.DTO;

import com.studyParty.entity.Source;
import com.studyParty.entity.group.GroupTask;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class GroupTaskDTO {
    private GroupTask groupTask;
    private List<Source> sources;
}
