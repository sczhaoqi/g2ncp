package com.demo.ncpdata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sczhaoqi
 * @date 2020/4/5 1:20
 */
@RestController
@RequestMapping("")
public class IndexController
{
    @GetMapping("")
    public String index()
    {
        return "index.html";
    }
}
