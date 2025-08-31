package com.studyParty.group.services;


import com.baomidou.mybatisplus.extension.service.IService;
import com.studyParty.entity.group.DTO.GroupJoinDTO;
import com.studyParty.entity.group.GroupJoin;

import java.util.List;

public interface GroupJoinServer extends IService<GroupJoin> {
    List<List<GroupJoinDTO>> findMyGroups(Long userId);
    boolean agreeJoin(Long groupId, Long userId, GroupJoin groupJoin);
    boolean disagreeJoin(Long groupJoinId, Long userId, GroupJoin groupJoin);
    boolean isJoined(Long groupId, Long userId);
}