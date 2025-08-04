package com.studyParty.group.services.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.group.Group;
import com.studyParty.group.mapper.GroupMapper;
import com.studyParty.group.services.GroupServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServerImpl extends ServiceImpl<GroupMapper, Group> implements GroupServer {

    @Autowired
    private GroupMapper groupMapper;
    @Override
    public List<Group> findMyGroups(Long id) {
        return groupMapper.findMyGroups(id);
    }
}
