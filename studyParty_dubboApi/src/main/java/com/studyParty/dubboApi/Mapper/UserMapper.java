package com.studyParty.dubboApi.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.user.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<User> selectGroupUser(Long groupId, Long userId);
}
