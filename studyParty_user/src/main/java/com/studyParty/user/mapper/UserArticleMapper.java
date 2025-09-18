package com.studyParty.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.user.UserArticle;
import com.studyParty.entity.user.UserTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserArticleMapper extends BaseMapper<UserArticle> {
}
