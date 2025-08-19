package com.studyParty.entity.task.DTO;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TaskDTO {
    private Long id;
    private Long uploader;
    private String title;
    private String context;
    private Integer isOver;
    private Long isTrueId;
    private Long starCoin;
    private Long starPrestige;
    private Timestamp createTime;
    private Integer status;
    private String name;
    private String head;
    private String school;
}
