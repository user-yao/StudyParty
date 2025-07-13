package com.studyParty.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.studyParty.user.Utils.MyFileUtil;
import com.studyParty.user.Utils.PasswordEncoder;
import com.studyParty.user.Utils.RedisUtil;
import com.studyParty.user.common.Result;
import com.studyParty.user.domain.User;
import com.studyParty.user.domain.entity.UserToken;
import com.studyParty.user.mapper.UserMapper;
import com.studyParty.user.services.UserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
/// 登录
/// 注册
/// 更新头像
/// 修改密码
/// 更新个人信息
@RestController
public class UserController {
    @Autowired
    private MyFileUtil fileUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserServer userServer;
    @Value("${head}")
    private String head;
    @Value("${saveHead}")
    private String saveHead;
    @GetMapping("/login")
    public Result<?> login(String phone, String password){
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone",phone);
            User userByPhone = userMapper.selectOne(queryWrapper);
            if(userByPhone == null){
                return Result.error("用户名不存在");
            }
            if(!PasswordEncoder.matches(password,userByPhone.getPassword())){
                return Result.error("用户名或密码错误");
            }
            String jwt = userServer.login(phone, password);
            UserToken userToken = new UserToken(userByPhone,jwt);
            Logger.getGlobal().log(Level.WARNING,jwt);
            if (StringUtils.hasLength(jwt)){
                if (!Objects.equals(userByPhone.getLastLogin(), Date.valueOf(LocalDate.now()))){
                    userMapper.update(null,new LambdaUpdateWrapper<User>()
                            .eq(User::getId,userByPhone.getId())
                            .set(User::getLastLogin, Date.valueOf(LocalDate.now())));
                }
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
            queryWrapper.eq("phone",user.getPhone());
            User userByPhone = userMapper.selectOne(queryWrapper);
            if(userByPhone != null){
                return Result.error("用户名已存在");
            }
            user.setHead(head + (user.getSex().equals("男") ? "boys.png" : "girls.png"));
            String encodedPassword = PasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userServer.save(user);
            return  Result.success("注册成功");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Result.error();
        }
    }
    @PostMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody User user){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",user.getId());
        String oldEncodedPassword = userMapper.selectOne(queryWrapper).getPassword();
        if(oldEncodedPassword.equals(PasswordEncoder.encode(user.getPassword()))){
            return Result.error("新密码与旧密码一致");
        }
        user.setPassword(PasswordEncoder.encode(user.getPassword()));
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId,user.getId())
                .set(User::getPassword,user.getPassword())
        );
        return Result.success("密码修改成功");
    }
    @PostMapping("/updateHead")
    public Result<?> updateHead(@RequestParam("photo")MultipartFile photo,@RequestParam("id") int id){
        String photoName = photo.getOriginalFilename();
        String newName = null;
        if (photoName != null && photoName.contains(".")) {
            String[] parts = photoName.split("\\.");
            newName = "." + parts[parts.length - 1]; // 获取最后一个部分作为扩展名
        }
        String uuid =  id + "_" + UUID.randomUUID().toString().replace("-","");
        String phName = uuid + newName;
        String pre = saveHead + id;
        String path = pre + "/" + phName;
        File file = new File(pre);
        try {
            if(!file.exists() && !file.isDirectory()){
                Files.createDirectories(Path.of(path));
                photo.transferTo(new File(path));
            }else {
                fileUtil.deleteDirectoryRecursively(pre);
                Files.createDirectories(Path.of(path));
                photo.transferTo(new File(path));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        userMapper.update(null,new LambdaUpdateWrapper<User>()
                .eq(User::getId,id)
                .set(User::getHead, head + id +"/" +phName)
                );
        return Result.success(head + id +"/" +phName);
    }

    @PostMapping("/updateUser")
    public Result<?> updateUser(@RequestBody User user){
        if (user.getPhone() == null){
            return Result.error("请输入手机号");
        }
        if (user.getPhone().length() != 11){
            return Result.error("手机号长度有误");
        }
        if (!user.getPhone().matches("^1[3-9]\\d{8}$")){
            return Result.error("手机号格式有误");
        }
        userMapper.update(null,new LambdaUpdateWrapper<User>()
                .eq(User::getId,user.getId())
                .set(User::getPhone,user.getPhone())
                .set(User::getName,user.getName())
                .set(User::getSex,user.getSex())
                .set(User::getMajor,user.getMajor())
                .set(User::getGrade,user.getGrade())
                .set(User::getEmail,user.getEmail())
                .set(User::getSchool,user.getSchool())
        );
        return Result.success();
    }
}
