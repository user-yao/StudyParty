package com.studyParty.entity.group.DTO;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class GroupTaskAnswerDTO {
    private Long id;
    private Long groupTaskId;
    private Long userId;
    private String context;
    private Timestamp time;
    private int haveSource;
    private int score;
    private String username;
    private String head;
    private String status;
    private String school;
}
