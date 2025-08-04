package com.studyParty.group.services;


import com.baomidou.mybatisplus.extension.service.IService;
import com.studyParty.entity.group.Group;

import java.util.List;

public interface GroupServer extends IService<Group> {
    List<Group> findMyGroups(Long id);
}
