package com.studyParty.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.studyParty.entity.friend.Friend;
import com.studyParty.entity.friend.FriendRequest;
import com.studyParty.user.common.Result;
import com.studyParty.user.mapper.FriendMapper;
import com.studyParty.user.mapper.FriendRequestMapper;
import com.studyParty.user.services.FriendRequestServer;
import com.studyParty.user.services.FriendServer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class UserFriendController {
    private final FriendServer friendServer;
    private final FriendRequestServer friendRequestServer;
    private final FriendRequestMapper friendRequestMapper;
    private final FriendMapper friendMapper;

    @PostMapping("/request")
    public Result<?> request(Long friendId, String context, @RequestHeader("X-User-Id") String userId) {
        if (friendId == null){
            return Result.error("请选择好友");
        }
        if (friendId.equals(Long.valueOf(userId))){
            return Result.error("不能添加自己为好友");
        }
        if (friendServer.isFriend(Long.valueOf(userId), friendId)){
            return Result.error("已经是好友");
        }
        friendRequestServer.save(new FriendRequest(Long.valueOf(userId), friendId, context));
        return Result.success();
    }
    @PostMapping("/accept")
    public Result<?> accept(Long applicant, Integer isConsent, @RequestHeader("X-User-Id") String userId) {
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", applicant);
        queryWrapper.eq("friend_id", Long.valueOf(userId));
        queryWrapper.eq("status", 0);
        FriendRequest friendRequest = friendRequestMapper.selectOne(queryWrapper);
        if (friendRequest == null){
            return Result.error("请求不存在");
        }
        if(friendRequest.getIsConsent() != 0){
            return Result.error("请求已处理");
        }
        if (isConsent == 1){
            friendRequest.setIsConsent(1);
            friendServer.save(new Friend(Long.valueOf(userId), applicant));
            friendServer.save(new Friend(applicant, Long.valueOf(userId)));
        }else if (isConsent == 2){
            friendRequest.setIsConsent(2);
        }
        friendRequestServer.updateById(friendRequest);
        return Result.success();
    }
    @PostMapping("/remark")
    public Result<?> remark(Long friendId, String remark, @RequestHeader("X-User-Id") String userId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.valueOf(userId));
        queryWrapper.eq("friend_id", friendId);
        Friend friend = friendMapper.selectOne(queryWrapper);
        if (friend == null){
            return Result.error("请选择好友");
        }
        friend.setRemark(remark);
        friendMapper.updateById(friend);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<?> delete(Long friendId, @RequestHeader("X-User-Id") String userId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.valueOf(userId));
        queryWrapper.eq("friend_id", friendId);
        friendMapper.delete(queryWrapper);
        queryWrapper.clear();
        queryWrapper.eq("user_id", friendId);
        queryWrapper.eq("friend_id", Long.valueOf(userId));
        friendMapper.delete(queryWrapper);
        return Result.success();
    }
    @PostMapping("/friendList")
    public Result<?> list(@RequestHeader("X-User-Id") String userId) {
        return Result.success(friendMapper.friendList(Long.valueOf(userId)));
    }
    @PostMapping("/friendRequestList")
    public Result<?> friendRequestList(@RequestHeader("X-User-Id") String userId) {
        return Result.success(friendRequestMapper.friendRequestList(Long.valueOf(userId)));
    }
    @PostMapping("/myFriendRequestList")
    public Result<?> myFriendRequestList(@RequestHeader("X-User-Id") String userId) {
        return Result.success(friendRequestMapper.myFriendRequestList(Long.valueOf(userId)));
    }
}
