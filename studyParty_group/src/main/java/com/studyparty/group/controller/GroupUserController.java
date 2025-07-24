package com.studyparty.group.controller;

import com.studyParty.entity.group.Group;
import com.studyparty.group.common.Result;
import com.studyparty.group.services.GroupServer;
import com.studyparty.group.services.GroupUserServer;
import com.studyparty.studyparty_dubboapi.services.BusinessServer;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/groupUser")
public class GroupUserController {
    @Autowired
    private GroupServer groupServer;
    @Autowired
    private GroupUserServer groupUserServer;
    @DubboReference
    private BusinessServer businessServer;

    @PostMapping("deleteUser")
    public Result<?> deleteUser(int groupId, int delUserId,  @RequestHeader("X-User-Id") String userId){
        Group group = groupServer.getById(groupId);
        if(group.getLeader() != Integer.parseInt(userId)){
            return Result.error("权限错误");
        }
        groupUserServer.removeById(delUserId);
        group.setPeopleNum(group.getPeopleNum() - 1);
        groupServer.updateById(group);
        return Result.success();
    }
    @PostMapping("selectGroupUser")
    public Result<?> selectUser(int groupId,  @RequestHeader("X-User-Id") String userId){
        return Result.success(businessServer.selectUser(groupId,userId));
    }
}
