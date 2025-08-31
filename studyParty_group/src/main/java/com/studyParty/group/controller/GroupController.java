package com.studyParty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.entity.group.Level;
import com.studyParty.entity.user.User;
import com.studyParty.entity.group.GroupJoin; // 新增导入
import com.studyParty.group.common.Result;
import com.studyParty.group.mapper.GroupMapper;
import java.sql.Date;
import com.studyParty.group.mapper.GroupJoinMapper; // 新增导入
import com.studyParty.group.mapper.GroupUserMapper;
import com.studyParty.group.services.GroupServer;
import com.studyParty.group.services.GroupUserServer;
import com.studyParty.dubboApi.services.BusinessServer;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.sql.Date;

/***
 * url:功能说明
 *  /searchGroup：搜索群组
 *  /getMyGroup：获取用户创建的群组和加入的群组
 *  /createGroup：创建群组
 *  /updateHead：更新群组头像
 *  /updateGroup：更新群组信息
 *  /deleteGroup：删除群组
 *  /transferGroup：群组转让
 *  /changeDeputy：更改群组当周负责人
 *  /changeCanJoin：更改群组可加入状态
 *  /contributionGroup：贡献群组 --- 完成任务加20积分
 *  /invitePredecessor：邀请群组前辈（老师，企业）
 *  /clearPredecessor：删除群组前辈
 */
@RestController
@RequiredArgsConstructor
public class GroupController {
    @Value("${head}")
    private String head;
    @Value("${saveHead}")
    private String saveHead;
    private final GroupMapper groupMapper;
    private final GroupServer groupServer;
    private final GroupUserMapper groupUserMapper;
    private final GroupJoinMapper groupJoinMapper; // 新增注入
    private final GroupUserServer groupUserServer;
    @DubboReference
    private BusinessServer businessServer;

    @PostMapping("/searchGroup")
    public Result<?> searchGroup(String searchContext, int currentPage, Integer canJoin) {
        if (currentPage <= 0) {
            currentPage = 1;
        }
        if (searchContext.trim().isEmpty()) {
            return Result.error("请输入正确的搜索内容");
        }
        Page<Group> page = new Page<>(currentPage, 10);
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        // 构建搜索条件，确保括号正确包含OR条件
        queryWrapper.and(wrapper -> wrapper
                .like(Group::getGroupName, searchContext.trim())
                .or()
                .like(Group::getSlogan, searchContext.trim()));

        // 添加canJoin过滤条件
        if (canJoin != null && canJoin == 1) {
            queryWrapper.eq(Group::getCanJoin, canJoin);
        }
        return Result.success(groupMapper.selectPage(page, queryWrapper));
    }

    @PostMapping("/getMyGroup")
    public Result<?> getMyGroup(@RequestHeader("X-User-Id") String userId) {
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("leader", userId);
        List<Group> leaderGroups = groupMapper.selectList(queryWrapper);
        List<Group> memberGroups = groupMapper.findMyGroups(Long.valueOf(userId));

        // 过滤掉同时作为leader的群组
        Set<Long> leaderGroupIds = leaderGroups.stream()
                .map(Group::getId)
                .collect(Collectors.toSet());

        List<Group> filteredMemberGroups = memberGroups.stream()
                .filter(group -> !leaderGroupIds.contains(group.getId()))
                .collect(Collectors.toList());

        List<List<Group>> list = new ArrayList<>();
        list.add(leaderGroups);
        list.add(filteredMemberGroups);
        return Result.success(list);
    }

    @PostMapping("/createGroup")
    public Result<?> createGroup(
            String groupName,
            String slogan,
            String rule,
            Integer canJoin,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestHeader("X-User-Id") String userId) {
        Group group = new Group(Long.valueOf(userId), groupName, slogan, rule, canJoin);
        if (group.getGroupName().trim().isEmpty()) {
            return Result.error("请输入正确的群组名称");
        }
        if (group.getSlogan().trim().isEmpty()) {
            return Result.error("请输入正确的群组标语");
        }
        if (group.getRule().trim().isEmpty()) {
            return Result.error("请输入正确的群组规则");
        }
        if (group.getLeader() != Integer.parseInt(userId)) {
            return Result.error("身份错误，请重新登录");
        }

        // 先保存群组信息，获取群组ID
        group.setHead(head + "group.png");
        groupServer.save(group);

        // 创建群组头像目录
        Path dirPath = Paths.get(saveHead, String.valueOf(group.getId()));
        Path targetPath = dirPath.resolve("groupHeadPhoto.png");

        try {
            // 确保目录存在
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 如果上传了头像，则使用上传的头像，否则复制默认头像
            if (photo != null && !photo.isEmpty()) {
                photo.transferTo(targetPath);
            } else {
                // 查找默认群组头像
                Path sourcePath = Paths.get(saveHead, "group.png");
                // 如果默认头像文件存在，则复制到群组目录
                if (Files.exists(sourcePath)) {
                    Files.copy(sourcePath, targetPath);
                }
            }

            // 更新群组头像路径
            groupMapper.update(null, new LambdaUpdateWrapper<Group>()
                    .eq(Group::getId, group.getId())
                    .set(Group::getHead, head + group.getId() + "/groupHeadPhoto.png"));
        } catch (IOException e) {
            // 即使头像处理失败，也继续创建群组流程
            System.err.println("处理群组头像失败: " + e.getMessage());
        }

        groupUserServer.save(new GroupUser(group.getId(), Long.valueOf(userId)));
        return Result.success();
    }

    @PostMapping("/updateHead")
    public Result<?> updateHead(@RequestParam("photo") MultipartFile photo, @RequestHeader("X-User-Id") String userId) {
        String path = saveHead + userId;
        File file = new File(path);
        try {
            if (file.exists() || file.isDirectory()) {
                Files.createDirectories(Path.of(path));
            }
            photo.transferTo(new File(path + "/groupHeadPhoto.png"));
            groupMapper.update(null, new LambdaUpdateWrapper<Group>()
                    .eq(Group::getLeader, Integer.parseInt(userId))
                    .set(Group::getHead, head + userId + "/groupHeadPhoto.png")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success(head + userId + "/groupHeadPhoto.png");
    }

    @PostMapping("/updateGroup")
    public Result<?> updateGroup(
            Long groupId,
            String slogan,
            String rule,
            String groupName,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestHeader("X-User-Id") String userId) {

        // 验证用户是否是群组的创建者
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getId, groupId);
        queryWrapper.eq(Group::getLeader, Integer.parseInt(userId));
        Group existingGroup = groupMapper.selectOne(queryWrapper);

        if (existingGroup == null) {
            return Result.error("未找到对应的群组或您不是群组创建者");
        }

        // 更新小组信息
        LambdaUpdateWrapper<Group> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Group::getId, groupId);

        if (groupName != null && !groupName.trim().isEmpty()) {
            updateWrapper.set(Group::getGroupName, groupName);
        }
        if (slogan != null && !slogan.trim().isEmpty()) {
            updateWrapper.set(Group::getSlogan, slogan);
        }
        if (rule != null && !rule.trim().isEmpty()) {
            updateWrapper.set(Group::getRule, rule);
        }
        groupMapper.update(null, updateWrapper);

        // 如果提供了头像，则更新头像
        if (photo != null && !photo.isEmpty()) {
            String path = saveHead + groupId;
            File file = new File(path);
            try {
                if (!file.exists()) {
                    Files.createDirectories(Path.of(path));
                }
                photo.transferTo(new File(path + "/groupHeadPhoto.png"));
                groupMapper.update(null, new LambdaUpdateWrapper<Group>()
                        .eq(Group::getId, groupId)
                        .set(Group::getHead, head + groupId + "/groupHeadPhoto.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return Result.success();
    }

    @PostMapping("/deleteGroup")
    public Result<?> deleteGroup(Long groupId, @RequestHeader("X-User-Id") String userId) {
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
    public Result<?> transferGroup(Long groupId, Long newLeader, @RequestHeader("X-User-Id") String userId) {
        if (groupMapper.selectById(groupId).getLeader() != Integer.parseInt(userId)) {
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
    public Result<?> changeDeputy(Long groupId, Integer deputy, @RequestHeader("X-User-Id") String userId) {
        if (groupMapper.selectById(groupId).getLeader() != Integer.parseInt(userId)) {
            return Result.error("权限错误");
        }
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        queryWrapper.eq("group_user", deputy);
        if (groupUserMapper.selectOne(queryWrapper) == null) {
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
    public Result<?> changeCanJoin(Long groupId, Integer canJoin, @RequestHeader("X-User-Id") String userId) {
        if (groupMapper.selectById(groupId).getLeader() != Integer.parseInt(userId)) {
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
    public Result<?> contributionGroup(Long groupId, @RequestHeader("X-User-Id") String userId) {
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("groupId", groupId);
        queryWrapper.eq("userId", userId);
        GroupUser groupUser = groupUserMapper.selectOne(queryWrapper);
        if (groupUser == null) {
            return Result.error("未找到对应的群组成员");
        }
        groupUser.setContribution(groupUser.getContribution() + 20);
        UpdateWrapper<GroupUser> queryWrapper1 = new UpdateWrapper<>();
        queryWrapper1.eq("id", groupUser.getId());
        queryWrapper1.set("contribution", groupUserMapper.selectById(userId).getContribution() + 20);
        if (groupUserMapper.update(null, queryWrapper1) == 0) {
            return Result.error("未找到对应的群组，可能已被删除");
        }
        UpdateWrapper<Group> queryWrapper2 = new UpdateWrapper<>();
        queryWrapper2.eq("id", groupId);
        Group group = groupMapper.selectById(groupId);
        int experience = group.getExperience() + 20;
        if (experience + 20 >= group.getNeedExperience()) {
            if (group.getGroupLevel() < 10) {
                Level level = Level.getLevel(group.getGroupLevel() + 1);
                group.setGroupLevel(level.getLevel());
                queryWrapper2.set("need_experience", level.getNeedExperience());
                queryWrapper2.set("max_people_num", level.getMaxPeopleNum());
                queryWrapper2.set("experience", experience + 20 - group.getNeedExperience());
            } else {
                queryWrapper2.set("experience", group.getNeedExperience());
            }
        } else {
            queryWrapper2.set("experience", experience + 20);
        }
        groupMapper.update(null, queryWrapper2);
        return Result.success();
    }

    @PostMapping("/invitePredecessor")
    public Result<?> inviteTeacher(Long groupId, Long predecessorId, int status, @RequestHeader("X-User-Id") String userId) {
        Group group = groupMapper.selectById(groupId);
        if (group.getLeader() != Integer.parseInt(userId)) {
            return Result.error("权限错误");
        }
        if (status == 1) {
            return Result.error("只能邀请老师与企业");
        }
        User user = businessServer.selectUserById(predecessorId);
        if (user.getStatus() == status) {
            return Result.error("身份错误");
        }
        if (status == 2) {
            group.setTeacher(predecessorId);
            groupMapper.updateById(group);
            groupUserServer.saveOrUpdate(new GroupUser(groupId, predecessorId));
        }
        if (status == 3) {
            group.setEnterprise(predecessorId);
            groupMapper.updateById(group);
            groupUserServer.saveOrUpdate(new GroupUser(groupId, predecessorId));
        }
        return Result.success();
    }

    @PostMapping("/clearPredecessor")
    public Result<?> clearPredecessor(Long groupId, Long predecessorId, int status, @RequestHeader("X-User-Id") String userId) {
        Group group = groupMapper.selectById(groupId);
        if (group.getLeader() != Integer.parseInt(userId)) {
            return Result.error("权限错误");
        }
        if (status == 1) {
            return Result.error("只能选择老师与企业");
        }
        if (status == 2) {
            group.setTeacher(0L);
        }
        if (status == 3) {
            group.setEnterprise(0L);
        }
        groupMapper.updateById(group);
        groupUserMapper.delete(new QueryWrapper<GroupUser>().eq("groupId", groupId).eq("userId", predecessorId));
        return Result.success();
    }

    @PostMapping("/selectGroupById")
    public Result<?> selectGroupById(Long groupId) {
        if (groupMapper.selectById(groupId) == null) {
            return Result.error("未找到对应的群组");
        }
        return Result.success(groupMapper.selectById(groupId));
    }
    
    @PostMapping("/inviteUserToGroup")
    public Result<?> inviteUserToGroup(Long groupId, Long invitedUserId, @RequestHeader("X-User-Id") String userId) {
        // 检查发起邀请的用户是否是小组的创建者或管理员
        Group group = groupMapper.selectById(groupId);
        if (group == null) {
            return Result.error("未找到对应的群组");
        }
        
        Long leaderId = group.getLeader();
        Long deputyId = group.getDeputy();
        
        if (!leaderId.toString().equals(userId) && !deputyId.toString().equals(userId)) {
            return Result.error("权限错误，只有群主或副群主可以邀请用户");
        }
        
        // 检查被邀请用户是否存在
        User invitedUser = businessServer.selectUserById(invitedUserId);
        if (invitedUser == null) {
            return Result.error("被邀请用户不存在");
        }
        
        // 检查被邀请用户是否已经在群组中
        QueryWrapper<GroupUser> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("group_id", groupId);
        checkWrapper.eq("group_user", invitedUserId);
        if (groupUserMapper.selectCount(checkWrapper) > 0) {
            return Result.error("该用户已在群组中");
        }
        
        // 检查是否已经发送过邀请
        QueryWrapper<GroupJoin> joinCheckWrapper = new QueryWrapper<>();
        joinCheckWrapper.eq("group_id", groupId);
        joinCheckWrapper.eq("user_id", invitedUserId);
        joinCheckWrapper.eq("is_invited", 1); // 邀请类型
        joinCheckWrapper.eq("is_pass", 0); // 待审核状态
        if (groupJoinMapper.selectCount(joinCheckWrapper) > 0) {
            return Result.error("已经发送过邀请，等待用户处理");
        }
        
        // 根据用户身份区分处理
        int userStatus = invitedUser.getStatus();
        String userRole = "";
        switch (userStatus) {
            case 1: // 普通学生
                userRole = "学生";
                break;
            case 2: // 老师
                userRole = "老师";
                // 检查是否已经设置了老师
                if (group.getTeacher() != null && group.getTeacher() > 0) {
                    return Result.error("该群组已设置老师");
                }
                break;
            case 3: // 企业
                userRole = "企业";
                // 检查是否已经设置了企业
                if (group.getEnterprise() != null && group.getEnterprise() > 0) {
                    return Result.error("该群组已设置企业");
                }
                break;
            default:
                userRole = "未知身份用户";
                break;
        }
        
        // 创建邀请记录
        GroupJoin groupJoin = new GroupJoin(groupId, group.getLeader(), invitedUserId, "邀请加入", 1);
        groupJoinMapper.insert(groupJoin);
        
        // 如果是老师或企业，需要预先设置相关字段
        switch (userStatus) {
            case 2: // 老师
                group.setTeacher(invitedUserId);
                groupMapper.updateById(group);
                break;
            case 3: // 企业
                group.setEnterprise(invitedUserId);
                groupMapper.updateById(group);
                break;
        }
        
        return Result.success("已邀请" + userRole + "加入群组");
    }
    
    @PostMapping("/acceptGroupInvitation")
    public Result<?> acceptGroupInvitation(Long groupJoinId, @RequestHeader("X-User-Id") String userId) {
        // 检查邀请记录是否存在
        GroupJoin groupJoin = groupJoinMapper.selectById(groupJoinId);
        if (groupJoin == null) {
            return Result.error("未找到邀请记录");
        }
        
        // 检查是否是被邀请用户本人操作
        if (!groupJoin.getUserId().toString().equals(userId)) {
            return Result.error("权限错误");
        }
        
        // 检查是否是邀请类型
        if (groupJoin.getIsInvited() != 1) {
            return Result.error("不是邀请记录");
        }
        
        // 检查邀请是否已处理
        if (groupJoin.getIsPass() != 0) {
            return Result.error("邀请已处理");
        }
        
        // 检查群组是否存在
        Group group = groupMapper.selectById(groupJoin.getGroupId());
        if (group == null) {
            return Result.error("未找到对应的群组");
        }
        
        // 检查群组是否已满
        if (group.getPeopleNum() >= group.getMaxPeopleNum()) {
            return Result.error("群组已满");
        }
        
        // 检查用户是否已经加入了群组（通过其他方式）
        QueryWrapper<GroupUser> userCheckWrapper = new QueryWrapper<>();
        userCheckWrapper.eq("group_id", groupJoin.getGroupId());
        userCheckWrapper.eq("group_user", Long.valueOf(userId));
        if (groupUserMapper.selectCount(userCheckWrapper) > 0) {
            // 更新邀请状态为已通过
            groupJoin.setIsPass(1);
            groupJoinMapper.updateById(groupJoin);
            return Result.error("您已经加入了该群组");
        }
        
        // 添加用户到群组
        GroupUser groupUser = new GroupUser(groupJoin.getGroupId(), groupJoin.getUserId());
        groupUser.setAddTime(new java.sql.Date(System.currentTimeMillis()));
        groupUserMapper.insert(groupUser);
        
        // 更新邀请状态为已通过
        groupJoin.setIsPass(1);
        groupJoinMapper.updateById(groupJoin);
        
        // 更新群组人数
        group.setPeopleNum(group.getPeopleNum() + 1);
        groupMapper.updateById(group);
        
        return Result.success("成功加入群组");
    }
    
    @PostMapping("/rejectGroupInvitation")
    public Result<?> rejectGroupInvitation(Long groupJoinId, @RequestHeader("X-User-Id") String userId) {
        // 检查邀请记录是否存在
        GroupJoin groupJoin = groupJoinMapper.selectById(groupJoinId);
        if (groupJoin == null) {
            return Result.error("未找到邀请记录");
        }
        
        // 检查是否是被邀请用户本人操作
        if (!groupJoin.getUserId().toString().equals(userId)) {
            return Result.error("权限错误");
        }
        
        // 检查是否是邀请类型
        if (groupJoin.getIsInvited() != 1) {
            return Result.error("不是邀请记录");
        }
        
        // 检查邀请是否已处理
        if (groupJoin.getIsPass() != 0) {
            return Result.error("邀请已处理");
        }
        
        // 检查群组是否存在
        Group group = groupMapper.selectById(groupJoin.getGroupId());
        if (group == null) {
            return Result.error("未找到对应的群组");
        }
        
        // 更新邀请状态为已拒绝
        groupJoin.setIsPass(2);
        groupJoinMapper.updateById(groupJoin);
        
        // 如果是老师或企业，需要清除相关设置
        Long userIdLong = Long.valueOf(userId);
        if (group.getTeacher() != null && group.getTeacher().equals(userIdLong)) {
            group.setTeacher(0L);
            groupMapper.updateById(group);
        }
        
        if (group.getEnterprise() != null && group.getEnterprise().equals(userIdLong)) {
            group.setEnterprise(0L);
            groupMapper.updateById(group);
        }
        
        return Result.success("已拒绝群组邀请");
    }

}