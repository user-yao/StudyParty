package com.studyParty.user.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.studyParty.user.domain.User;

public interface UserServer extends IService<User> {
    String login(String username, String password);
}
