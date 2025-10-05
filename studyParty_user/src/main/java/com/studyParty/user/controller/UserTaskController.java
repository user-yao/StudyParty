package com.studyParty.user.controller;

import com.studyParty.dubboApi.services.BusinessServer;
import com.studyParty.entity.user.DTO.UserTaskGroup;
import com.studyParty.entity.user.DTO.UserTaskTask;
import com.studyParty.user.mapper.UserTaskMapper;
import com.studyParty.user.common.Result;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/userTask")
public class UserTaskController {
    private final UserTaskMapper userTaskMapper;
    @DubboReference
    private BusinessServer businessServer;
    @GetMapping("/selectMyUserTasks")
    public Result<?> selectMyUserTasks(@RequestHeader("X-User-Id") String userId){
        List<UserTaskTask> userTaskTasks = businessServer.selectUserTaskTask(Long.parseLong(userId));
        List<UserTaskGroup> userTaskGroups = businessServer.selectUserTaskGroup(Long.parseLong(userId));
        List<List<?>> list = new ArrayList<>();
        list.add(userTaskTasks);
        list.add(userTaskGroups);
        return Result.success(list);
    }
}
