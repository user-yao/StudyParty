package com.studyParty.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.group.DTO.GroupJoinDTO;
import com.studyParty.entity.group.GroupJoin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupJoinMapper extends BaseMapper<GroupJoin> {
    List<GroupJoinDTO> findMyGroupsWithDetails(@Param("userId") Long userId);
    List<GroupJoinDTO> findMyApplicationsWithDetails(@Param("userId") Long userId);
}