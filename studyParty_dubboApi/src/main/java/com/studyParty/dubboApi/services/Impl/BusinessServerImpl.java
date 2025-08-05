package com.studyParty.dubboApi.services.Impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.studyParty.entity.user.User;
import com.studyParty.dubboApi.Mapper.UserMapper;
import com.studyParty.dubboApi.services.BusinessServer;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@DubboService
public class BusinessServerImpl implements BusinessServer {
    @Autowired
    private UserMapper userMapper;
    @Override
    @DubboService
    public List<User> selectUser(Long groupId, Long userId) {
        return userMapper.selectGroupUser(groupId, userId);
    }
    @DubboService
    @Override
    public User selectUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @DubboService
    public void addStarPrestige(Long userId, Long starPrestige) {
        User user = userMapper.selectById(userId);
        UpdateWrapper<User> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("id", userId);
        queryWrapper.set("star_prestige", user.getStarPrestige()+starPrestige);
        userMapper.update(queryWrapper);
    }

}
