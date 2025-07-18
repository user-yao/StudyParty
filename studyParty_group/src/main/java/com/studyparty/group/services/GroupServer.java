package com.studyparty.group.services;


import com.baomidou.mybatisplus.extension.service.IService;
import com.studyparty.group.domian.Group;

import java.util.List;

public interface GroupServer extends IService<Group> {
    List<Group> findMyGroups(int id);
}
