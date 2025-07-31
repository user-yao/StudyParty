package com.studyparty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.entity.user.User;
import com.studyparty.group.common.Result;
import com.studyparty.group.mapper.GroupMapper;
import com.studyparty.group.mapper.GroupUserMapper;
import com.studyparty.group.services.GroupServer;
import com.studyparty.group.services.GroupUserServer;
import com.studyparty.studyparty_dubboapi.services.BusinessServer;
import org.apache.dubbo.config.annotation.DubboReference;
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
    @DubboReference
    private BusinessServer businessServer;

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
    @PostMapping("/transferGroup")
    public Result<?> transferGroup(int groupId, int newLeader, @RequestHeader("X-User-Id") String userId) {
        if (groupMapper.selectById(groupId).getLeader()!= Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
        UpdateWrapper<Group> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("id", groupId);
        queryWrapper.set("leader", newLeader);
        if (groupMapper.update(null, queryWrapper) == 0) {
            return Result.error("未找到对应的群组，可能已被删除");
        }
        return Result.success();
    }
    @PostMapping("/changeDeputy")
    public Result<?> changeDeputy(int groupId, int deputy, @RequestHeader("X-User-Id") String userId) {
        if (groupMapper.selectById(groupId).getLeader()!= Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("groupId", groupId);
        queryWrapper.eq("userId", deputy);
        if (groupUserMapper.selectOne(queryWrapper) == null){
            return Result.error("未找到对应的群组成员");
        }
        UpdateWrapper<Group> queryWrapper1 = new UpdateWrapper<>();
        queryWrapper1.eq("id", groupId);
        queryWrapper1.set("deputy", deputy);
        if (groupMapper.update(null, queryWrapper1) == 0) {
            return Result.error("未找到对应的群组，可能已被删除");
        }
        return Result.success();
    }
    @PostMapping("/changeCanJoin")
    public Result<?> changeCanJoin(int groupId, int canJoin, @RequestHeader("X-User-Id") String userId) {
        if (groupMapper.selectById(groupId).getLeader()!= Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
        UpdateWrapper<Group> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("id", groupId);
        queryWrapper.set("canJoin", canJoin);
        if (groupMapper.update(null, queryWrapper) == 0) {
            return Result.error("未找到对应的群组，可能已被删除");
        }
        return Result.success();
    }
    @PostMapping("/contributionGroup")
    public Result<?> contributionGroup(int groupId, @RequestHeader("X-User-Id") String userId) {
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("groupId", groupId);
        queryWrapper.eq("userId", userId);
        if (groupUserMapper.selectOne(queryWrapper) == null){
            return Result.error("未找到对应的群组成员");
        }
        UpdateWrapper<GroupUser> queryWrapper1 = new UpdateWrapper<>();
        queryWrapper1.eq("groupId", groupId);
        queryWrapper1.eq("userId", userId);
        queryWrapper1.set("contribution", groupUserMapper.selectById(userId).getContribution() + 20);
        if (groupUserMapper.update(null, queryWrapper1) == 0) {
            return Result.error("未找到对应的群组，可能已被删除");
        }
        UpdateWrapper<Group> queryWrapper2 = new UpdateWrapper<>();
        queryWrapper2.eq("id", groupId);
        queryWrapper2.set("experience", groupMapper.selectById(groupId).getExperience() + 20);
        return Result.success();
    }
    @PostMapping("/invitePredecessor")
    public Result<?> inviteTeacher(int groupId, int predecessorId, int status, @RequestHeader("X-User-Id") String userId) {
        Group group = groupMapper.selectById(groupId);
        if (group.getLeader()!= Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
        if (status == 1){
            return Result.error("只能邀请老师与企业");
        }
        User user = businessServer.selectUserById(predecessorId);
        if(user.getStatus() == status ){
            return Result.error("身份错误");
        }
        if (status == 2){
            group.setTeacher(predecessorId);
            groupMapper.updateById(group);
            groupUserServer.saveOrUpdate(new GroupUser(groupId, predecessorId));
        }
        if (status == 3){
            group.setEnterprise(predecessorId);
            groupMapper.updateById(group);
            groupUserServer.saveOrUpdate(new GroupUser(groupId, predecessorId));
        }
        return Result.success();
    }
    @PostMapping("/clearPredecessor")
    public Result<?> clearPredecessor(int groupId, int predecessorId, int status, @RequestHeader("X-User-Id") String userId) {
        Group group = groupMapper.selectById(groupId);
        if (group.getLeader()!= Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
        if (status == 1){
            return Result.error("只能选择老师与企业");
        }
        if (status == 2){
            group.setTeacher(0);
        }
        if (status == 3){
            group.setEnterprise(0);
        }
        groupMapper.updateById(group);
        groupUserMapper.delete(new QueryWrapper<GroupUser>().eq("groupId", groupId).eq("userId", predecessorId));
        return Result.success();
    }
}
