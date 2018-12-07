package com.pyg.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.mapper.TbUserMapper;
import com.pyg.pojo.TbUser;
import com.pyg.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import utils.HttpClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void sendSms(String phone) throws Exception {
        HttpClient httpClient = new HttpClient("http://127.0.0.1:7788/sms/sendSms");

        httpClient.addParameter("phoneNumbers", phone);
        httpClient.addParameter("signName", "品位优雅购物");
        httpClient.addParameter("templateCode", "SMS_130926832");
        String numeric = RandomStringUtils.randomNumeric(4);
        httpClient.addParameter("templateParam", "{\"code\":\"" + numeric + "\"}");
        httpClient.post();
        System.out.println(numeric);
        String content = httpClient.getContent();
        System.out.println(content);
        redisTemplate.boundValueOps("sms_" + phone).set(numeric, 300, TimeUnit.SECONDS);
    }

    @Override
    public void add(TbUser user, String code) {
        String numeric = (String) redisTemplate.boundValueOps("sms_" + user.getPhone()).get();

        if (numeric == null) {
            throw new RuntimeException("验证码已失效");
        }

        if (!numeric.equals(code)) {
            throw new RuntimeException("验证码输入错误");
        }

        String password = user.getPassword();
        user.setPassword(DigestUtils.md5Hex(password));
        user.setCreated(new Date());
        user.setUpdated(new Date());

        userMapper.insert(user);
        redisTemplate.delete("sms_" + user.getPhone());
    }
}
