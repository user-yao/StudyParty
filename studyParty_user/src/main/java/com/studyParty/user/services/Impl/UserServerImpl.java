package com.studyParty.user.services.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyParty.user.Utils.TokenUtil;
import com.studyParty.user.domain.User;
import com.studyParty.user.mapper.UserMapper;
import com.studyParty.user.services.UserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServerImpl extends ServiceImpl<UserMapper, User> implements UserServer {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TokenUtil tokenUtil;
    @Override
    public String login(String phone, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",phone);
        User userByName = userMapper.selectOne(queryWrapper);
        if(userByName == null){
            return null;
        }
        return tokenUtil.createToken(userByName);
    }
}
