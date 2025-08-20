package com.studyParty.user.services.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.friend.Friend;
import com.studyParty.user.mapper.FriendMapper;
import com.studyParty.user.services.FriendServer;
import org.springframework.stereotype.Service;

@Service
public class FriendServerImpl extends ServiceImpl<FriendMapper, Friend> implements FriendServer {
    @Override
    public boolean isFriend(Long userId, Long friendId) {
        Friend friend = new Friend(userId, friendId);
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("friend_id", friendId);
        return this.getOne(queryWrapper) != null;
    }
}
