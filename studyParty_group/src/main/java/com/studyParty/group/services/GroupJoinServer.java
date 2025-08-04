package com.studyParty.group.services;


import com.baomidou.mybatisplus.extension.service.IService;
import com.studyParty.entity.group.GroupJoin;

import java.util.List;

public interface GroupJoinServer extends IService<GroupJoin> {
    List<List<GroupJoin>> findMyGroups(Long userId);
    boolean agreeJoin(Long id, Long userId, GroupJoin groupJoin);
    boolean disagreeJoin(Long id, Long userId, GroupJoin groupJoin);
    boolean isJoined(Long groupId, Long userId);
}
