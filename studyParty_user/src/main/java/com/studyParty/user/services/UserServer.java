package com.studyParty.user.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.studyParty.entity.user.User;
import com.studyParty.user.domain.entity.UserToken;

public interface UserServer extends IService<User> {
    UserToken login(String phone, String password);
}
