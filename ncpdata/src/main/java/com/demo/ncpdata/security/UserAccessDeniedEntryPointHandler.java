package com.demo.ncpdata.security;

import com.demo.ncpdata.common.SocialType;
import com.demo.ncpdata.service.SocialTokenService;
import com.google.common.base.Strings;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author sczhaoqi
 * @date 2020/4/5 20:41
 */
@Component
public class UserAccessDeniedEntryPointHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler
{

    @Autowired
    private SocialTokenService socialTokenService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException
    {
        execute(request, response);
    }

    private void execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        request.setCharacterEncoding(Charsets.UTF_8.name());
        if (SecurityContextHolder.getContext().getAuthentication() == null
                ||
                "anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            SocialType type = SocialType.UNKNOWN;
            switch (request.getRequestURI()) {
                case "/user/githubLogin":
                    if (!Strings.isNullOrEmpty(request.getParameter("code"))) {
                        // request accessToken
                        type = SocialType.GITHUB;
                        String token = socialTokenService.getAccessToken(type, request.getParameterMap());
                        response.setStatus(HttpStatus.OK.value());
                        response.getWriter().write(token);
                        // get user info
                        // gen token
                        break;
                    }
                default:
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write(
                            socialTokenService.getUserAuthURL(SocialType.GITHUB)
                    );

                    break;
            }
        }
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException
    {
        execute(request, response);
    }
}
