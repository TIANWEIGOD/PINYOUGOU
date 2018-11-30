package com.pyg.manager.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {


    @RequestMapping("/name")
    public Map<String, String> name() {
        SecurityContext context = SecurityContextHolder.getContext();
        User principal = (User) context.getAuthentication().getPrincipal();
        String username = principal.getUsername();

        Map<String, String> map = new HashMap<>();
        map.put("loginName", username);
        return map;
    }


}
