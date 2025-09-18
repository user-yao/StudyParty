package com.studyParty.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.user.UserCreateTask;
import com.studyParty.entity.user.UserTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCreateTaskMapper extends BaseMapper<UserCreateTask> {
}
