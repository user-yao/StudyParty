package com.studyParty.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studyParty.entity.group.DTO.GroupTaskAnswerDTO;
import com.studyParty.entity.group.GroupTaskAnswer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupTaskAnswerMapper extends BaseMapper<GroupTaskAnswer> {
    List<GroupTaskAnswerDTO> selectGroupTaskAnswerWithUser(Long groupTaskId);
    GroupTaskAnswerDTO selectMyGroupTaskAnswerWithUser(Long groupTaskId, Long userId);
}
