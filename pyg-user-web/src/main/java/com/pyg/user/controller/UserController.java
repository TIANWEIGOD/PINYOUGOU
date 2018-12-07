package com.pyg.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbUser;
import com.pyg.user.service.UserService;
import com.pyg.utils.PygResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/sendSms")
    public PygResult sendSms(String phone) {
        try {
            userService.sendSms(phone);
            return new PygResult(true, "发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "发送失败");
        }
    }

    @RequestMapping("/add")
    public PygResult add(@RequestBody TbUser user, String code) {
        try {
            userService.add(user, code);
            return new PygResult(true, "注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, e.getMessage());
        }
    }

}
