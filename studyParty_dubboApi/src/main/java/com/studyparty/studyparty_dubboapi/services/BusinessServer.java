package com.studyparty.studyparty_dubboapi.services;

import com.studyParty.entity.user.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface BusinessServer {
    List<User> selectUser(Long groupId, Long userId);

    User selectUserById(Long userId);
}
