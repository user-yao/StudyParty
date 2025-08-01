package com.studyparty.studyparty_dubboapi.services.Impl;

import com.studyParty.entity.user.User;
import com.studyparty.studyparty_dubboapi.Mapper.UserMapper;
import com.studyparty.studyparty_dubboapi.services.BusinessServer;
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
}
