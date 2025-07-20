package com.studyparty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyparty.group.common.Result;
import com.studyparty.group.domian.Group;
import com.studyparty.group.domian.GroupUser;
import com.studyparty.group.mapper.GroupMapper;
import com.studyparty.group.mapper.GroupUserMapper;
import com.studyparty.group.services.GroupJoinServer;
import com.studyparty.group.services.GroupServer;
import com.studyparty.group.services.GroupUserServer;
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

@RestController("/group")
public class GroupController {
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupServer groupServer;
    @Value("${head}")
    private String head;
    @Value("${saveHead}")
    private String saveHead;
    @Autowired
    private GroupUserMapper groupUserMapper;
    @Autowired
    private GroupUserServer groupUserServer;

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
    public Result<?> createGroup(@RequestBody Group group, @RequestHeader("X-User-Id") String userId) {
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
        groupServer.save(group);
        groupUserServer.save(new GroupUser(Integer.parseInt(userId), group.getId()));
        return Result.success();
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
    @PostMapping("/updateGroup")
    public Result<?> updateGroup(String slogan, String rule, String groupName, @RequestHeader("X-User-Id") String userId) {
        LambdaUpdateWrapper<Group> updateWrapper = new LambdaUpdateWrapper<>();
        if (!groupName.trim().isEmpty()) {
            updateWrapper.set(Group::getGroupName, groupName);
        }
        if (!slogan.trim().isEmpty()) {
            updateWrapper.set(Group::getSlogan, slogan);
        }
        if (!rule.trim().isEmpty()) {
            updateWrapper.set(Group::getRule, rule);
        }
        updateWrapper.eq(Group::getLeader, Integer.parseInt(userId));
        return Result.success(groupMapper.update(null, updateWrapper));
    }
    @PostMapping("/deleteGroup")
    public Result<?> deleteGroup(int groupId, @RequestHeader("X-User-Id") String userId) {
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getId, groupId);
        queryWrapper.eq(Group::getLeader, Integer.parseInt(userId));
        if (groupMapper.delete(queryWrapper) == 0) {
            groupUserMapper.delete(new UpdateWrapper<GroupUser>().eq("groupId", groupId));
            return Result.error("未找到对应的群组，可能已被删除");
        }
        return Result.success();
    }
}
