package com.studyParty.dubboApi.services;

import com.studyParty.entity.user.User;

import java.util.List;

public interface BusinessServer {
    List<User> selectUser(Long groupId, Long userId);

    User selectUserById(Long userId);
    void addStarPrestige(Long userId, Long starPrestige);
}
