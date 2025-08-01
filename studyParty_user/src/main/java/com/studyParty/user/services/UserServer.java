package com.studyParty.user.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.studyParty.entity.user.DTO.UserToken;
import com.studyParty.entity.user.User;

public interface UserServer extends IService<User> {
    UserToken login(String phone, String password);
}
