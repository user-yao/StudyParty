package com.studyParty.entity.task.DTO;

import com.studyParty.entity.task.Task;
import com.studyParty.entity.user.User;
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

    public TaskDTO(Task task, User  user) {
        this.id = task.getId();
        this.uploader = task.getUploader();
        this.title = task.getTitle();
        this.context = task.getContext();
        this.isOver = task.getIsOver();
        this.isTrueId = task.getIsTrueId();
        this.starCoin = task.getStarCoin();
        this.starPrestige = task.getStarPrestige();
        this.createTime = task.getCreateTime();
        this.status = task.getStatus();
        this.name = user.getName();
        this.head = user.getHead();
        this.school = user.getSchool();
    }
}
