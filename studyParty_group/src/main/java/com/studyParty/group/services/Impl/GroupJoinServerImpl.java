package com.studyParty.group.services.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupJoin;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.group.mapper.GroupJoinMapper;
import com.studyParty.group.mapper.GroupMapper;
import com.studyParty.group.mapper.GroupUserMapper;
import com.studyParty.group.services.GroupJoinServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<List<GroupJoin>> findMyGroups(Long userId) {
        List<List<GroupJoin>> list = new ArrayList<>();
        QueryWrapper<GroupJoin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_leader", userId);
        list.add(groupJoinMapper.selectList(queryWrapper));
        queryWrapper.clear();
        queryWrapper.eq("user_id", userId);
        list.add(groupJoinMapper.selectList(queryWrapper));
        return list;
    }

    @Override
    public boolean agreeJoin(Long groupId, Long userId, GroupJoin groupJoin) {
        Group group = groupMapper.selectById(groupId);
        if (group.getPeopleNum() < group.getMaxPeopleNum()) {
            group.setPeopleNum(group.getPeopleNum() + 1);
            groupJoin.setIsPass(1);
            return groupJoinMapper.update(groupJoin, new QueryWrapper<GroupJoin>().eq("id", groupJoin.getId())) == 1 &&
                    groupMapper.updateById(group) == 1 &&
                    groupUserMapper.insert(new GroupUser(groupId, groupJoin.getUserId())) == 1;
        }
        return false;
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
        if (groupJoinMapper.selectOne(queryWrapper) != null) {
            return true;
        }
        return false;
    }
}
