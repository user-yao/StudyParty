package com.studyParty.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studyParty.entity.task.DTO.TaskAnswerDTO;
import com.studyParty.entity.task.TaskAnswer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskAnswerMapper extends BaseMapper<TaskAnswer> {
    IPage<TaskAnswerDTO> selectTaskAnswerWithUser(IPage<TaskAnswerDTO> page,Long taskId);
    TaskAnswerDTO selectTaskTrueAnswer(Long taskId);
}
