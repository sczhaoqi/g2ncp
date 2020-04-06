package com.demo.ncpdata.service;

import com.alibaba.fastjson.JSON;
import com.demo.ncpdata.common.SocialType;
import com.demo.ncpdata.security.User;
import com.demo.ncpdata.utils.JwtTokenUtil;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author sczhaoqi
 * @date 2020/4/6 0:13
 */
@Service
public class SocialTokenService
{
    @Autowired
    private GithubConfig github;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private Cache<String, String> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS).build();
    private Cache<String, String> accessTokenCache = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS).build();
    private RestTemplate socialTokenRestTemplate = new RestTemplateBuilder().build();

    public String getAccessToken(SocialType type, Map<String, String[]> params)
            throws AccessTokenRequiredException
    {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        String token = null;
        switch (type) {
            case GITHUB:
                if (accessTokenCache.getIfPresent(params.get("code")[0]) != null) {
                    token =accessTokenCache.getIfPresent(params.get("code")[0]);
                }
                else {
                    form.addAll("code", Arrays.asList(params.get("code")));
                    form.add("client_id", github.getClientId());
                    form.add("client_secret", github.getClientSecret());
                    form.add("redirect_uri", github.getPreEstablishedRedirectUri());
                    headers.add("Accept", "application/json");
                    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);
                    String res = socialTokenRestTemplate.postForObject(github.getAccessTokenUri(), requestEntity, String.class);
                    if (requestEntity.hasBody()) {
                        token = Objects.requireNonNull(JSON.parseObject(res, GithubAccessToken.class)).getSocialToken();
                        accessTokenCache.put(params.get("code")[0], token);
                    }
                }
                GithubUser user = null;
                if (!Strings.isNullOrEmpty(token)) {
                    user = socialTokenRestTemplate.getForObject(github.getUserInfoUri() + "?access_token=" + token, GithubUser.class);
                }
                if (user != null && !Strings.isNullOrEmpty(user.getUsername())) {
                    User userInfo = (User) userDetailsService.loadUserByUsername(user.getUsername());
                    token = jwtTokenUtil.generateToken(userInfo);
                }
                break;
            default:
                break;
        }
        return token;
    }

    public String getUserAuthURL(SocialType type)
            throws UnsupportedEncodingException
    {
        return github.getUserAuthorizationUri() +
                "?client_id=" + github.getClientId() +
                "&redirect_uri=" + URLEncoder.encode(github.getPreEstablishedRedirectUri(), Charsets.UTF_8.name()) +
                "&response_type=code";
    }
}
