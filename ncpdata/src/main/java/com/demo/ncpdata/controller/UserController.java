package com.demo.ncpdata.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sczhaoqi
 * @date 2020/4/5 1:25
 */
@RestController
@RequestMapping("user")
public class UserController
{
    @GetMapping(value = "/githubLogin")
    public String login()
    {

        return "登陆成功";
    }

    @GetMapping("info")
    public Object userInfo()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return JSON.toJSONString(authentication);
    }
}
