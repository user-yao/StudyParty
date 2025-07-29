package com.studyparty.group.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyParty.entity.group.GroupTask;
import com.studyparty.group.common.Result;
import com.studyparty.group.mapper.GroupTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/groupTask")
public class GroupTaskController {
    @Autowired
    private  GroupTaskMapper groupTaskMapper;

    @PostMapping("/selectMyGroupTask")
    public Result<?> selectMyGroupTask( String groupId, Integer currentPage) {
        if (groupId == null) {
            return Result.error("参数错误");
        }
        if (currentPage == null || currentPage < 1) {
            currentPage = 1;
        }
        Page<GroupTask> page = new Page<>(currentPage, 10);
        QueryWrapper<GroupTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("groupId", groupId);
        return Result.success(groupTaskMapper.selectPage(page, queryWrapper));
    }

}
