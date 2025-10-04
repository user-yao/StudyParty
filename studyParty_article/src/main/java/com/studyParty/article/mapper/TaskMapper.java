package com.studyParty.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studyParty.entity.task.DTO.TaskDTO;
import com.studyParty.entity.task.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    IPage<TaskDTO> selectTaskWithUser(IPage<TaskDTO> page, String searchContext);
    IPage<TaskDTO> recommendTask(IPage<TaskDTO> page, Long userId);
}
