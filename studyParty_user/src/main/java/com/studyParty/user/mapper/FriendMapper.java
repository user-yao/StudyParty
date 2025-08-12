package com.studyParty.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.friend.DTO.FriendDTO;
import com.studyParty.entity.friend.Friend;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendMapper extends BaseMapper<Friend> {
    List<FriendDTO> friendList(Long userId);
}
