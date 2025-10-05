package com.studyParty.dubboApi.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyParty.entity.user.UserArticle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserArticleMapper extends BaseMapper<UserArticle> {
}
