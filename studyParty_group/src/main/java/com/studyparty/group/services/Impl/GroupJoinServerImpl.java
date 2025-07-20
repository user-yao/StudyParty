package com.studyparty.group.services.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyparty.group.domian.Group;
import com.studyparty.group.domian.GroupJoin;
import com.studyparty.group.domian.GroupUser;
import com.studyparty.group.mapper.GroupJoinMapper;
import com.studyparty.group.mapper.GroupMapper;
import com.studyparty.group.mapper.GroupUserMapper;
import com.studyparty.group.services.GroupJoinServer;
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
    public List<List<GroupJoin>> findMyGroups(int userId) {
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
    public boolean agreeJoin(int groupId, int userId, GroupJoin groupJoin) {
        Group group = groupMapper.selectById(groupId);
        if (group.getPeopleNum() < group.getMaxPeopleNum()) {
            group.setPeopleNum(group.getPeopleNum() + 1);
            groupJoin.setIsPass(1);
            return groupJoinMapper.update(groupJoin, new QueryWrapper<GroupJoin>().eq("id", groupJoin.getId())) == 1 &&
                    groupMapper.updateById(group) == 1 &&
                    groupUserMapper.insert(new GroupUser(groupId, userId)) == 1;
        }
        return false;
    }

    @Override
    public boolean disagreeJoin(int groupJoinId, int userId, GroupJoin groupJoin) {
        groupJoin.setIsPass(2);
        return groupJoinMapper.update(groupJoin, new QueryWrapper<GroupJoin>().eq("id", groupJoinId)) == 1;
    }
}
