package com.studyParty.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.friend.FriendRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendRequestMapper extends BaseMapper<FriendRequest> {
    List<FriendRequest> friendRequestList(Long userId);

    List<FriendRequest> myFriendRequestList(Long userId);
}
