package com.studyparty.group.controller;

import com.studyparty.group.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/groupTask")
public class GroupTaskController {
    @PostMapping("/addTask")
    public Result<?> addTask(String taskName, String taskContext, int groupId, @RequestHeader("X-User-Id") String userId){


    }

}
