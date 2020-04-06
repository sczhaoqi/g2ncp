package com.demo.ncpdata.common;

/**
 * @author sczhaoqi
 * @date 2020/4/6 0:21
 */
public enum SocialType
{
    UNKNOWN("unknown"),
    GITHUB("github"),
    WECHAT("wechat");
    private String type;

    SocialType(String type)
    {
        this.type = type;
    }
}
