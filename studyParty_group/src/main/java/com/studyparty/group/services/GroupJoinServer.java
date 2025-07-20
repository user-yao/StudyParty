package com.studyparty.group.services;


import com.baomidou.mybatisplus.extension.service.IService;
import com.studyparty.group.domian.GroupJoin;

import java.util.List;

public interface GroupJoinServer extends IService<GroupJoin> {
    List<List<GroupJoin>> findMyGroups(int userId);
    boolean agreeJoin(int id, int userId, GroupJoin groupJoin);
    boolean disagreeJoin(int id, int userId, GroupJoin groupJoin);
}
