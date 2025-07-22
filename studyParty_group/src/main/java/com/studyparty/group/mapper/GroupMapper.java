package com.studyparty.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.group.Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {
    List<Group> findMyGroups(int id);
}
