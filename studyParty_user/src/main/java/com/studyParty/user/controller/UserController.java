package com.studyParty.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.studyParty.user.mapper.UserArticleMapper;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.DTO.UserToken;
import com.studyParty.entity.user.User;
import com.studyParty.entity.user.UserArticle;
import com.studyParty.entity.user.UserTask;
import com.studyParty.user.Utils.MyFileUtil;
import com.studyParty.user.Utils.PasswordEncoder;
import com.studyParty.user.Utils.RedisUtil;
import com.studyParty.user.common.Result;

import com.studyParty.user.mapper.FriendMapper;
import com.studyParty.user.mapper.UserMapper;
import com.studyParty.user.mapper.UserTaskMapper;
import com.studyParty.user.services.UserServer;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

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
    private final FriendMapper friendMapper;
    private final UserTaskMapper userTaskMapper;
    private final UserArticleMapper userArticleMapper;
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
                userToken.getUser().setFinishTask(userTaskMapper.selectCount(new QueryWrapper<UserTask>().eq("user_id",userToken.getUser().getId())));
                return Result.success(userToken);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Logger.getGlobal().log(Level.WARNING,e.getMessage());
            return Result.error("用户名或密码错误");
        }
        return Result.error("用户名或密码错误");
    }
    @GetMapping("/userInfo")
    public Result<?> userInfo(@RequestHeader("X-User-Id") String userId){
        try {
            User user = userMapper.selectById(Long.parseLong(userId));
            user.setFinishTask(userTaskMapper.selectCount(new QueryWrapper<UserTask>().eq("user_id",userId)));
            user.setArticleNum(userArticleMapper.selectCount(new QueryWrapper<UserArticle>().eq("user_id",userId)));
            user.setTaskNum(userTaskMapper.selectCount(new QueryWrapper<UserTask>().eq("user_id",userId)));
            return Result.success(user);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Result.error();
        }
    }
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user){
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();//封装查询条件
            queryWrapper.eq("phone",user.getPhone());
            User userByPhone = userMapper.selectOne(queryWrapper);
            if(userByPhone != null){
                return Result.error("用户已存在");
            }
            user.setStarCoin(100L);
            user.setGroupCoin(0L);
            user.setStarPrestige(0L);
            user.setClockIn(1);
            user.setFinishTask(0L);
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
    public Result<?> updatePassword(String password,@RequestHeader("X-User-Id") String userId){
        User user = userMapper.selectById(Long.parseLong(userId));
        String oldEncodedPassword = user.getPassword();
        if(oldEncodedPassword.equals(PasswordEncoder.encode(password))){
            return Result.error("新密码与旧密码一致");
        }
        user.setPassword(PasswordEncoder.encode(password));
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId,Long.parseLong(userId))
                .set(User::getPassword,user.getPassword())
        );
        return Result.success("密码修改成功");
    }


    @PostMapping("/updateUser")
    public Result<?> updateUser(String name, String sex, String major, String grade, String phone, String school, String email,@RequestHeader("X-User-Id") String userId){
        if (phone == null){
            return Result.error("请输入手机号");
        }
        if (phone.length() != 11){
            return Result.error("手机号长度有误");
        }
        User user = userMapper.selectById(Long.parseLong(userId));
        userMapper.update(null,new LambdaUpdateWrapper<User>()
                .eq(User::getId,Long.parseLong(userId))
                .set(User::getName,name)
                .set(User::getSex,sex)
                .set(User::getMajor,major)
                .set(User::getGrade,grade)
                .set(User::getPhone,phone)
                .set(User::getSchool,school)
                .set(User::getEmail,email)
        );
        return Result.success();
    }
    @PostMapping("/updateHead")
    public Result<?> updateHead(@RequestParam(value = "photo", required = false) MultipartFile photo, @RequestHeader("X-User-Id") String userId) {
        // 防止非法字符导致路径穿越
        if (userId.contains("..") || userId.contains(File.separator)) {
            return Result.error("非法用户ID");
        }
        
        // 检查是否有上传文件
        if (photo == null || photo.isEmpty()) {
            return Result.error("未提供有效的头像文件");
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

            // 保存上传的文件
            photo.transferTo(targetFile);
            
            // 更新用户头像路径
            userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .eq(User::getId, Long.parseLong(userId))
                    .set(User::getHead, head + userId + "/userHeadPhoto.png"));

            return Result.success(head + userId + "/userHeadPhoto.png");

        } catch (IOException e) {
            return Result.error("文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("系统错误: " + e.getMessage());
        }
    }

    @PostMapping("/selectUser")
    public Result<?> selectUser(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Long id,
                                @RequestHeader("X-User-Id") String userId) {
        // 验证userId
        if (userId == null || userId.isEmpty()) {
            return Result.error("用户未登录");
        }

        Long currentUserId;
        try {
            currentUserId = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return Result.error("用户ID格式错误");
        }

        // 如果提供了id参数，则进行精确查询
        if (id != null) {
            User user = userMapper.selectById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            UserDTO friend = buildUserDTO(user, currentUserId);
            return Result.success(friend);
        }

        // 如果没有提供查询参数，则返回错误
        if (keyword == null || keyword.isEmpty()) {
            return Result.error("请提供查询参数");
        }

        // 构造查询条件，模糊匹配name字段，精确匹配phone字段
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword).or()
                .eq("phone", keyword);

        List<User> users = userMapper.selectList(queryWrapper);
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO friend = buildUserDTO(user, currentUserId);
            userDTOs.add(friend);
        }
        return Result.success(userDTOs);
    }

    private UserDTO buildUserDTO(User user, Long currentUserId) {
        UserDTO friend = friendMapper.selectUserAndRemarkById(user.getId().longValue(), currentUserId);
        boolean isFriend = friendMapper.isFriend(currentUserId, user.getId().longValue());
        if (friend == null) {
            friend = new UserDTO(null, user);
        }
        friend.setFinishTask(userTaskMapper.selectCount(new QueryWrapper<UserTask>().eq("user_id", user.getId())));
        friend.setArticleNum(userArticleMapper.selectCount(new QueryWrapper<UserArticle>().eq("user_id", user.getId())));
        friend.setFriend(isFriend);
        return friend;
    }
}
