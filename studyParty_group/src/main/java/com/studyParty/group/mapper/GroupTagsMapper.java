package com.studyParty.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studyParty.entity.group.DTO.GroupTaskDTO;
import com.studyParty.entity.group.GroupTags;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupTagsMapper extends BaseMapper<GroupTags> {
}
