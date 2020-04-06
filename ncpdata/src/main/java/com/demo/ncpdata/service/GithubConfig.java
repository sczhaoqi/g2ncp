package com.demo.ncpdata.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sczhaoqi
 * @date 2020/4/6 11:33
 */
@Data
@Component
@ConfigurationProperties(prefix = "social.github")
public class GithubConfig
{
    private String clientId;
    private String clientSecret;
    private String accessTokenUri;
    private String userAuthorizationUri;
    private String clientAuthenticationScheme;
    private String preEstablishedRedirectUri;
    private String userInfoUri;
}
