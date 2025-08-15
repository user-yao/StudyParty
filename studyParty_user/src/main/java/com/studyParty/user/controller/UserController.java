package com.studyParty.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.studyParty.entity.user.DTO.UserToken;
import com.studyParty.entity.user.User;
import com.studyParty.user.Utils.MyFileUtil;
import com.studyParty.user.Utils.PasswordEncoder;
import com.studyParty.user.Utils.RedisUtil;
import com.studyParty.user.common.Result;

import com.studyParty.user.mapper.UserMapper;
import com.studyParty.user.services.UserServer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
/// 登录
/// 注册
/// 更新头像
/// 修改密码
/// 更新个人信息
@RestController
@RequiredArgsConstructor
public class UserController {
    private final MyFileUtil fileUtil;
    private final RedisUtil redisUtil;
    private final UserMapper userMapper;
    private final UserServer userServer;
    @Value("${head}")
    private String head;
    @Value("${saveHead}")
    private String saveHead;
    @GetMapping("/login")
    public Result<?> login(String phone, String password){
        try {
            UserToken userToken = userServer.login(phone, password);
            if (StringUtils.hasLength(userToken.getToken())){
                Date lastLogin = userToken.getUser().getLastLogin();
                if (lastLogin == null){
                    userToken.getUser().setClockIn(1);
                }else{
                    LocalDate lastLoginDate = lastLogin.toLocalDate();
                    if (ChronoUnit.DAYS.between(lastLoginDate, LocalDate.now()) == 1 ){
                        userToken.getUser().setClockIn(userToken.getUser().getClockIn() + 1);
                    }else if (ChronoUnit.DAYS.between(lastLoginDate, LocalDate.now()) > 1){
                        userToken.getUser().setClockIn(1);
                    }
                }
                redisUtil.saveToRedis(userToken.getToken(),userToken.getUser().getPassword());
                userMapper.update(null,new LambdaUpdateWrapper<User>()
                        .eq(User::getId,userToken.getUser().getId())
                        .set(User::getLastLogin, Date.valueOf(LocalDate.now()))
                        .set(User::getClockIn,userToken.getUser().getClockIn()));
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

            // 先保存用户信息，获取用户ID
            String encodedPassword = PasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setCreateDate(Date.valueOf(LocalDate.now()));
            userServer.save(user);
            
            // 创建用户头像目录并复制默认头像
            Path dirPath = Paths.get(saveHead, String.valueOf(user.getId()));
            Path targetPath = dirPath.resolve("userHeadPhoto.png");
            File targetFile = targetPath.toFile();
            
            try {
                // 确保目录存在
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
                
                // 根据性别选择默认头像
                String defaultAvatar = user.getSex().equals("男") ? "boys.png" : "girls.png";
                Path sourcePath = Paths.get(saveHead, defaultAvatar);
                
                // 如果默认头像文件存在，则复制到用户目录
                if (Files.exists(sourcePath)) {
                    Files.copy(sourcePath, targetPath);
                }
                
                // 更新用户头像路径
                userMapper.update(null, new LambdaUpdateWrapper<User>()
                        .eq(User::getId, user.getId())
                        .set(User::getHead, head + user.getId() + "/userHeadPhoto.png"));
                
            } catch (IOException e) {
                Logger.getGlobal().log(Level.WARNING, "复制默认头像失败: " + e.getMessage());
                // 即使头像复制失败，也继续注册流程
            }

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
    @PostMapping("/updateHead")
    public Result<?> updateHead(@RequestParam("photo") MultipartFile photo, @RequestHeader("X-User-Id") String userId) {
        // 防止非法字符导致路径穿越
        if (userId.contains("..") || userId.contains(File.separator)) {
            return Result.error("非法用户ID");
        }

        // 使用 Paths 构建安全路径
        Path dirPath = Paths.get(saveHead, userId);
        Path targetPath = dirPath.resolve("userHeadPhoto.png");
        File targetFile = targetPath.toFile();

        try {
            // 确保目录存在
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 更新用户头像路径
            userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .eq(User::getId, userId)
                    .set(User::getHead, head + userId + "/userHeadPhoto.png"));

            // 保存上传的文件
            photo.transferTo(targetFile);
            return Result.success(head + userId + "/userHeadPhoto.png");

        } catch (IOException e) {
            return Result.error("文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("系统错误: " + e.getMessage());
        }
    }


   /* @PostMapping("/updateHead")
    public Result<?> updateHead(@RequestParam("photo")MultipartFile photo,@RequestParam("id") int id,@RequestParam("oldPhoto") String oldPhoto){
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
                new File(oldPhoto).delete();
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
    }*/
}
