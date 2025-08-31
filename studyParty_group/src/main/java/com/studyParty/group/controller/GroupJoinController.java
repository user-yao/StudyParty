package com.studyParty.group.controller;

import com.studyParty.entity.group.DTO.GroupJoinDTO;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupJoin;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.group.common.Result;
import com.studyParty.group.mapper.GroupJoinMapper;
import com.studyParty.group.mapper.GroupMapper;
import com.studyParty.group.mapper.GroupUserMapper;
import com.studyParty.group.services.GroupJoinServer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

/***
 * url:功能说明
 * /joinGroup:申请加入群组
 * /getGroupJoin:获取用户申请加入的群组
 * /agreeJoin:同意加入群组
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/groupJoin")
public class GroupJoinController {
    private final GroupJoinMapper groupJoinMapper;
    private final GroupJoinServer groupJoinServer;
    private final GroupMapper groupMapper;
    private final GroupUserMapper groupUserMapper;

    @PostMapping("/joinGroup")
    public Result<?> joinGroup(Long groupId, String context, @RequestHeader("X-User-Id") String userId){
        GroupJoin groupJoin = new GroupJoin(groupId,Long.valueOf(userId),context);
        if(groupJoin.getUserId() != Integer.parseInt(userId)){
            return Result.error("用户身份错误");
        }
        Group group = groupMapper.selectById(groupJoin.getGroupId());
        if(group == null){
            return Result.error("群组不存在");
        }
        if (Objects.equals(groupJoin.getUserId(), group.getLeader())){
            return Result.error("不能申请加入自己的群");
        }
        if (group.getPeopleNum() >= group.getMaxPeopleNum()){
            return Result.error("群组已满");
        }
        if (groupJoinServer.isJoined(groupJoin.getGroupId(),groupJoin.getUserId())){
            return Result.error("已经申请加入");
        }
        if(group.getCanJoin() == 0){
            return Result.error("群组不允许加入");
        }
        groupJoin.setGroupLeader(group.getLeader());
        groupJoinMapper.insert(groupJoin);
        return Result.success();
    }
    
    @GetMapping("/getGroupJoin")
    @PostMapping("/getGroupJoin")
    public Result<?> getGroupJoin(@RequestHeader("X-User-Id") String userId){
        List<List<GroupJoinDTO>> result = groupJoinServer.findMyGroups(Long.valueOf(userId));
        return Result.success(result);
    }

    @PostMapping("/agreeJoin")
    public Result<?> agreeJoin(Long groupJoinId, Boolean agree, @RequestHeader("X-User-Id") String userId) {
        // 检查申请记录是否存在
        GroupJoin groupJoin = groupJoinMapper.selectById(groupJoinId);
        if (groupJoin == null) {
            return Result.error("未找到申请记录");
        }

        // 检查申请是否已处理
        if (groupJoin.getIsPass() != 0) {
            return Result.error("申请已处理");
        }

        // 判断是处理别人加入我的小组，还是处理邀请我加入别人的小组
        boolean isInvite = groupJoin.getIsInvited() == 1; // 是否为邀请类型

        if (isInvite) {
            // 处理邀请我加入别人的小组
            // 检查是否是被邀请用户本人操作
            if (!groupJoin.getUserId().toString().equals(userId)) {
                return Result.error("权限错误");
            }

            if (Boolean.TRUE.equals(agree)) {
                // 接受邀请
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
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<GroupUser> userCheckWrapper = 
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
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
                groupUser.setAddTime(new Date(System.currentTimeMillis()));
                groupUserMapper.insert(groupUser);

                // 更新邀请状态为已通过
                groupJoin.setIsPass(1);
                groupJoinMapper.updateById(groupJoin);

                // 更新群组人数
                group.setPeopleNum(group.getPeopleNum() + 1);
                groupMapper.updateById(group);

                return Result.success("成功加入群组");
            } else {
                // 拒绝邀请
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
        } else {
            // 处理别人申请加入我的小组
            if (!groupJoin.getGroupLeader().toString().equals(userId)) {
                return Result.error("用户权限不足");
            }

            if (Boolean.TRUE.equals(agree)) {
                if (groupJoinServer.agreeJoin(groupJoinId, groupJoin.getUserId(), groupJoin)) {
                    return Result.success();
                }
            } else {
                if (groupJoinServer.disagreeJoin(groupJoinId, groupJoin.getUserId(), groupJoin)) {
                    return Result.success();
                }
            }
            return Result.error();
        }
    }
    
    @PostMapping("/cancelJoin")
    public Result<?> cancelJoin(Long groupJoinId,@RequestHeader("X-User-Id") String userId){
        GroupJoin groupJoin = groupJoinMapper.selectById(groupJoinId);
        if (groupJoin.getIsPass() != 0){
            return Result.error("申请已处理");
        }
        if (groupJoin.getUserId() != Integer.parseInt(userId)){
            return Result.error("用户权限不足");
        }
        groupJoinMapper.deleteById(groupJoinId);
        return Result.success();
    }
}