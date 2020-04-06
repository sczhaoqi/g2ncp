package com.demo.ncpdata.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sczhaoqi
 * @date 2020/4/6 1:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubAccessToken
        implements SocialToken
{
    private String access_token;
    private String token_type;
    private String scope;

    @Override
    public String getSocialToken()
    {
        return this.getAccess_token();
    }
}
