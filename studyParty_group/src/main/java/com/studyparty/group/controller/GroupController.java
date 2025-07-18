package com.studyparty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyparty.group.common.Result;
import com.studyparty.group.domian.Group;
import com.studyparty.group.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GroupController {
    @Autowired
    private GroupMapper groupMapper;
    @PostMapping("/searchGroup")
    public Result<?> searchGroup(String searchContext, int currentPage,boolean canJoin) {
        if (currentPage <= 0){
            currentPage = 1;
        }
        if (searchContext.trim().isEmpty()){
            return Result.error("请输入正确的搜索内容");
        }
        Page<Group> page = new Page<>(currentPage, 10);
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Group::getGroupName, searchContext.trim());
        queryWrapper.or().like(Group::getSlogan, searchContext.trim());
        if (canJoin){
            queryWrapper.apply("people_num<max_people_unm");
        }
        return Result.success(groupMapper.selectPage(page, queryWrapper));
    }
    @PostMapping("/getMyGroup")
    public Result<?> getMyGroup(int userId) {
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("leader", userId);
        List<List<Group>> list = new ArrayList<>();
        list.add(groupMapper.findMyGroups(userId));
        list.add(groupMapper.selectList(queryWrapper));
        return Result.success(list);
    }

}
