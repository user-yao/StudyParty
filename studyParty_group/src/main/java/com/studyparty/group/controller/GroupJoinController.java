package com.studyparty.group.controller;

import com.studyParty.entity.group.Group;
import com.studyParty.entity.group.GroupJoin;
import com.studyparty.group.common.Result;
import com.studyparty.group.mapper.GroupJoinMapper;
import com.studyparty.group.mapper.GroupMapper;
import com.studyparty.group.services.GroupJoinServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("/groupJoin")
public class GroupJoinController {
    @Autowired
    private GroupJoinMapper groupJoinMapper;
    @Autowired
    private GroupJoinServer groupJoinServer;
    @Autowired
    private GroupMapper groupMapper;

    @PostMapping("/joinGroup")
    public Result<?> joinGroup(@RequestBody GroupJoin groupJoin, @RequestHeader("X-User-Id") String userId){
        if (groupJoin.getUserId() == groupJoin.getGroupLeader()){
            return Result.error("不能申请加入自己的群");
        }
        if(groupJoin.getUserId() != Integer.parseInt(userId)){
            return Result.error("用户身份错误");
        }
        Group group = groupMapper.selectById(groupJoin.getGroupId());
        if(group == null){
            return Result.error("群组不存在");
        }
        if (group.getPeopleNum() >= group.getMaxPeopleNum()){
            return Result.error("群组已满");
        }
        if (groupJoinServer.isJoined(groupJoin.getGroupId(),groupJoin.getUserId())){
            return Result.error("已经申请加入");
        }
        if(group.getCanJoin() == 0){
            return Result.error("群组不允许加入");
        }
        groupJoinMapper.insert(groupJoin);
        return Result.success();
    }
    @GetMapping("/getGroupJoin")
    public Result<?> getGroupJoin(@RequestHeader("X-User-Id") String userId){
        return Result.success(groupJoinServer.findMyGroups(Integer.parseInt(userId)));
    }

    @PostMapping("/agreeJoin")
    public Result<?> agreeJoin(int groupJoinId,boolean agree,@RequestHeader("X-User-Id") String userId){
        GroupJoin groupJoin = groupJoinMapper.selectById(groupJoinId);
        if (agree){
            if (groupJoin.getGroupLeader() != Integer.parseInt(userId)){
                return Result.error("用户权限不足");
            }
            if(groupJoinServer.agreeJoin(groupJoinId,Integer.parseInt(userId),groupJoin)){
                groupJoin.setIsPass(1);
                groupJoinMapper.updateById(groupJoin);
                return Result.success();
            }else{
                return Result.error("服务器错误");
            }
        }else{
            if(groupJoinServer.disagreeJoin(groupJoinId,Integer.parseInt(userId),groupJoin)){
                return Result.success();
            }else {
                return Result.error("服务器错误");
            }
        }
    }
}
