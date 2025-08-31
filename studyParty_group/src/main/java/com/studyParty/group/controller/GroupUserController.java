package com.studyParty.group.controller;

import com.studyParty.entity.group.Group;
import com.studyParty.group.common.Result;
import com.studyParty.group.services.GroupServer;
import com.studyParty.group.services.GroupUserServer;
import com.studyParty.dubboApi.services.BusinessServer;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/***
 * url:功能说明
 * /deleteUser:删除成员
 * /selectUser:查询用户
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/groupUser")
public class GroupUserController {
    private final GroupServer groupServer;
    private final GroupUserServer groupUserServer;
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
    public Result<?> selectUser(Long groupId,  @RequestHeader("X-User-Id") String userId){
        System.out.println(businessServer.selectUser(groupId, Long.valueOf(userId)).toString());
        return Result.success(businessServer.selectUser(groupId, Long.valueOf(userId)));
    }
}
