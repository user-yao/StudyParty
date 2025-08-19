package com.studyParty.entity.group.DTO;

import lombok.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class GroupTaskDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long groupId;
    private String groupTask;
    private String groupTaskUploader;
    private Timestamp groupTaskStartTime;
    private Timestamp groupTaskLastTime;
    private Long groupTaskFinish;
    private String groupTaskContext;
    private Long groupTaskUnfinished;
    private Timestamp createTime;
    private Long groupTaskId;
    private Long userId;
    private String context;
    private Timestamp time;
    private int haveSource;
    private int score;
}
