package com.studyparty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyparty.group.common.Result;
import com.studyparty.group.domian.Group;
import com.studyparty.group.mapper.GroupMapper;
import com.studyparty.group.services.GroupServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GroupController {
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupServer groupServer;
    @Value("${head}")
    private String head;
    @Value("${saveHead}")
    private String saveHead;

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
            queryWrapper.eq(Group::getCanJoin, 1);
        }
        return Result.success(groupMapper.selectPage(page, queryWrapper));
    }
    @GetMapping("/getMyGroup")
    public Result<?> getMyGroup(int userId) {
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("leader", userId);
        List<List<Group>> list = new ArrayList<>();
        list.add(groupMapper.findMyGroups(userId));
        list.add(groupMapper.selectList(queryWrapper));
        return Result.success(list);
    }
    @PostMapping("/createGroup")
    public Result<?> createGroup(Group group, @RequestHeader("X-User-Id") String userId) {
        if (group.getGroupName().trim().isEmpty()){
            return Result.error("请输入正确的群组名称");
        }
        if (group.getSlogan().trim().isEmpty()){
            return Result.error("请输入正确的群组标语");
        }
        if (group.getRule().trim().isEmpty()){
            return Result.error("请输入正确的群组规则");
        }
        if(group.getLeader() != Integer.parseInt(userId)){
            return Result.error("身份错误，请重新登录");
        }
        group.setHead(head + "group.png");
        return Result.success(groupServer.save(group));
    }
    @PostMapping("/updateHead")
    public Result<?> updateHead(@RequestParam("photo") MultipartFile photo, @RequestHeader("X-User-Id") String userId){
        String path = saveHead + userId;
        File file = new File(path);
        try {
            if (file.exists() || file.isDirectory()) {
                Files.createDirectories(Path.of(path));
            }
            photo.transferTo(new File(path+ "/groupHeadPhoto"));
            groupMapper.update(null,new LambdaUpdateWrapper<Group>()
                    .eq(Group::getLeader, Integer.parseInt(userId))
                    .set(Group::getHead, head + userId +"/groupHeadPhoto")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success(head + userId + "/groupHeadPhoto");
    }

}
