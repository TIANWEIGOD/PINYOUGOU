package com.pyg.user.service;

import com.pyg.pojo.TbUser;

import java.io.IOException;

public interface UserService {
    void sendSms(String phone) throws Exception;

    void add(TbUser user, String code);
}
