package com.studyParty.dubboApi.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.group.GroupTaskAnswer;
import com.studyParty.entity.task.TaskAnswer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupTaskAnswerMapper extends BaseMapper<GroupTaskAnswer> {
}
