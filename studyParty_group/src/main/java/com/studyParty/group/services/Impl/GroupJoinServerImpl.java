package com.studyParty.group.services.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.group.DTO.GroupJoinDTO;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupJoin;
import com.studyParty.entity.group.GroupTask;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.group.mapper.GroupJoinMapper;
import com.studyParty.group.mapper.GroupMapper;
import com.studyParty.group.mapper.GroupTaskMapper;
import com.studyParty.group.mapper.GroupUserMapper;
import com.studyParty.group.services.GroupJoinServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupJoinServerImpl extends ServiceImpl<GroupJoinMapper, GroupJoin> implements GroupJoinServer {
    @Autowired
    private GroupJoinMapper groupJoinMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupUserMapper groupUserMapper;
    @Autowired
    private GroupTaskMapper groupTaskMapper;

    @Override
    public List<List<GroupJoinDTO>> findMyGroups(Long userId) {
        List<List<GroupJoinDTO>> result = new ArrayList<>();
        
        // 第一个数组：需要我处理的申请（包括别人申请加入我的小组和邀请我加入的小组）
        List<GroupJoinDTO> pendingForMe = groupJoinMapper.findMyGroupsWithDetails(userId);
        
        // 第二个数组：我申请加入别人的群组和我邀请别人加入我的群组，需要别人处理的申请
        List<GroupJoinDTO> myPendingApplications = groupJoinMapper.findMyApplicationsWithDetails(userId);
        
        result.add(pendingForMe);
        result.add(myPendingApplications);
        
        return result;
    }

    @Override
    @Transactional
    public boolean agreeJoin(Long groupId, Long userId, GroupJoin groupJoin) {
        try {
            Group group = groupMapper.selectById(groupId);
            // 添加对group为null的检查
            if (group == null) {
                return false;
            }
            // 使用数据库层面的原子操作来确保并发安全
            // 先尝试更新group，只有当人数未满时才更新成功
            UpdateWrapper<Group> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", groupId)
                    .lt("people_num", group.getMaxPeopleNum())  // 正确使用maxPeopleNum的值
                    .setSql("people_num = people_num + 1");  // 原子性地增加人数
            int updated = groupMapper.update(null, updateWrapper);
            if (updated > 0) {

                groupJoin.setIsPass(1);
                UpdateWrapper<GroupTask> updateTaskWrapper = new UpdateWrapper<>();
                updateTaskWrapper.ge("group_task_start_time", new Timestamp(System.currentTimeMillis()))     // A字段 >= startTime
                        .le("group_task_last_time", new Timestamp(System.currentTimeMillis()))
                        .eq("group_id", groupJoin.getGroupId())// B字段 <= endTime
                        .setSql("group_task_unfinished = group_task_unfinished + 1");   // C字段自增1
                groupTaskMapper.update(null, updateTaskWrapper);
                return groupJoinMapper.update(groupJoin, new QueryWrapper<GroupJoin>().eq("id", groupJoin.getId())) == 1 &&
                        groupUserMapper.insert(new GroupUser(groupId, groupJoin.getUserId())) == 1;
            }
            return false;
        } catch (Exception e) {
            // 记录异常日志，方便问题排查
            e.printStackTrace();
            System.out.println("异常：" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean disagreeJoin(Long groupJoinId, Long userId, GroupJoin groupJoin) {
        groupJoin.setIsPass(2);
        return groupJoinMapper.update(groupJoin, new QueryWrapper<GroupJoin>().eq("id", groupJoinId)) == 1;
    }

    @Override
    public boolean isJoined(Long groupId, Long userId) {
        QueryWrapper<GroupJoin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId).eq("user_id", userId);
        return groupJoinMapper.selectCount(queryWrapper) > 0;
    }
}