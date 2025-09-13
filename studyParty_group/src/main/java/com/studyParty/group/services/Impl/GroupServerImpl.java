package com.studyParty.group.services.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupUser;
import com.studyParty.entity.group.Level;
import com.studyParty.group.common.Result;
import com.studyParty.group.mapper.GroupMapper;
import com.studyParty.group.mapper.GroupUserMapper;
import com.studyParty.group.services.GroupServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Service
public class GroupServerImpl extends ServiceImpl<GroupMapper, Group> implements GroupServer {

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupUserMapper groupUserMapper;
    @Override
    public List<Group> findMyGroups(Long id) {
        return groupMapper.findMyGroups(id);
    }
    public Result<?> contributionGroup(Long groupId, String userId) {
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        queryWrapper.eq("group_user", userId);
        GroupUser groupUser = groupUserMapper.selectOne(queryWrapper);
        if (groupUser == null) {
            return Result.error("未找到对应的群组成员");
        }
        groupUser.setContribution(groupUser.getContribution() + 20);
        if (groupUserMapper.updateById(groupUser) == 0) {
            return Result.error("未找到对应的群组，可能已被删除");
        }
        UpdateWrapper<Group> queryWrapper2 = new UpdateWrapper<>();
        queryWrapper2.eq("id", groupId);
        Group group = groupMapper.selectById(groupId);
        int experience = group.getExperience();
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
}
