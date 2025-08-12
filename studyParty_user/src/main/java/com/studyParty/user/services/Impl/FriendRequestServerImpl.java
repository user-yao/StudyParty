package com.studyParty.user.services.Impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.entity.friend.FriendRequest;
import com.studyParty.user.mapper.FriendRequestMapper;
import com.studyParty.user.services.FriendRequestServer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendRequestServerImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestServer {
}
