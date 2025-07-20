package com.studyparty.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyparty.group.domian.GroupJoin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupJoinMapper extends BaseMapper<GroupJoin> {
}
