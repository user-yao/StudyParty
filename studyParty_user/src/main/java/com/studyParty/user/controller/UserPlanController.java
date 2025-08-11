package com.studyParty.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.studyParty.entity.user.UserPlan;
import com.studyParty.user.common.Result;
import com.studyParty.user.mapper.UserPlanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/userPlan")
@RequiredArgsConstructor
public class UserPlanController {
    private final UserPlanMapper userPlanMapper;
    @PostMapping("/addUserPlan")
    public Result<?> addUserPlan(String planContext, Long startTime,  @RequestHeader("X-User-Id") String userId) {
        if (planContext == null || planContext.trim().isEmpty()) {
            return Result.error("计划内容不能为空");
        }
        if (startTime == null) {
            return Result.error("计划开始时间不能为空");
        }

        // 将毫秒时间戳转换为 Timestamp
        Timestamp startTimestamp = new Timestamp(startTime * 1000L);
        if(startTimestamp.before(new Timestamp(System.currentTimeMillis()))){
            return Result.error("计划开始时间不能早于当前时间");
        }
        Long num = userPlanMapper.selectCount(new QueryWrapper<UserPlan>().eq("user_id", userId));
        if (num >= 5){
            return Result.error("计划已达到上限");
        }
        userPlanMapper.insert(new UserPlan(planContext, startTimestamp, Long.valueOf(userId)));
        return Result.success();
    }
    @PostMapping("/deleteUserPlan")
    public Result<?> deleteUserPlan(Long planId, @RequestHeader("X-User-Id") String userId) {
        QueryWrapper<UserPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", planId);
        queryWrapper.eq("user_id", userId);
        if (userPlanMapper.selectOne(queryWrapper) == null){
            return Result.error("计划不存在");
        }
        userPlanMapper.delete(queryWrapper);
        return Result.success();
    }
    @PostMapping("/startUserPlan")
    public Result<?> startUserPlan(Long planId, @RequestHeader("X-User-Id") String userId) {
        QueryWrapper<UserPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", planId);
        queryWrapper.eq("user_id", userId);
        if (userPlanMapper.selectOne(queryWrapper) == null){
            return Result.error("计划不存在");
        }
        UserPlan userPlan = userPlanMapper.selectOne(queryWrapper);
        if (userPlan.getIsStart() == 1){
            return Result.error("计划已开始");
        }
        userPlan.setIsStart(1);
        userPlanMapper.updateById(userPlan);
        return Result.success();
    }
    @PostMapping("/endUserPlan")
    public Result<?> endUserPlan(Long planId, @RequestHeader("X-User-Id") String userId) {
        QueryWrapper<UserPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", planId);
        queryWrapper.eq("user_id", userId);
        if (userPlanMapper.selectOne(queryWrapper) == null){
            return Result.error("计划不存在");
        }
        UserPlan userPlan = userPlanMapper.selectOne(queryWrapper);
        if (userPlan.getIsStart() == 0){
            return Result.error("计划未开始");
        }
        userPlan.setIsStart(0);
        userPlan.setIsEnd(1);
        userPlanMapper.updateById(userPlan);
        return Result.success();
    }
    @PostMapping("/getUserPlans")
    public Result<?> getUserPlans(@RequestHeader("X-User-Id") String userId) {
        QueryWrapper<UserPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByAsc("start_time");
        queryWrapper.eq("is_end", 0);
        List<UserPlan> userPlans = userPlanMapper.selectList(queryWrapper);
        queryWrapper.clear();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("start_time");
        queryWrapper.eq("is_end", 1);
        List<UserPlan> userPlans1 = userPlanMapper.selectList(queryWrapper);
        List<List<UserPlan>> list = List.of(userPlans, userPlans1);
        return Result.success(list);
    }
}