package com.studyParty.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.studyParty.user.Utils.PasswordEncoder;
import com.studyParty.user.Utils.RedisUtil;
import com.studyParty.user.common.Result;
import com.studyParty.user.domain.User;
import com.studyParty.user.domain.entity.UserToken;
import com.studyParty.user.mapper.UserMapper;
import com.studyParty.user.services.UserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;
/// 登录
/// 注册
@RestController
public class UserController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserServer userServer;
    @GetMapping("/login")
    public Result<?> login(String name, String password){
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",name);
            User userByName = userMapper.selectOne(queryWrapper);
            if(userByName == null){
                return Result.error("用户名不存在");
            }
            if(!PasswordEncoder.matches(password,userByName.getPassword())){
                return Result.error("用户名或密码错误");
            }
            String jwt = userServer.login(name, password);
            UserToken userToken = new UserToken(userByName,jwt);
            Logger.getGlobal().log(Level.WARNING,jwt);
            if (StringUtils.hasLength(jwt)){
                return Result.success(userToken);
            }
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING,e.getMessage());
            return Result.error();
        }
        return Result.error("用户名或密码错误");
    }
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user){
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();//封装查询条件
            queryWrapper.eq("name",user.getName());
            User userByName = userMapper.selectOne(queryWrapper);
            if(userByName != null){
                return Result.error("用户名已存在");
            }
            String encodedPassword = PasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setStarCoin(100);
            user.setGroupCoin(0);
            user.setStarPrestige(0);
            userServer.save(user);
            return  Result.success("注册成功");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Result.error();
        }
    }


}
