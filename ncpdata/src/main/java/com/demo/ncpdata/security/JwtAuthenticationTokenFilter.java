package com.demo.ncpdata.security;

import com.demo.ncpdata.utils.JwtTokenUtil;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author sczhaoqi
 * @date 2020/4/5 19:28
 */
@Component
public class JwtAuthenticationTokenFilter
        extends OncePerRequestFilter
{

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        //先从url中取token
        String authToken = request.getParameter("token");
        String authHeader = request.getHeader(jwtTokenUtil.getHeader());
        if (!Strings.isNullOrEmpty(authHeader) && authHeader.startsWith(jwtTokenUtil.getHeader())) {
            //如果header中存在token，则覆盖掉url中的token
            authToken = authHeader.substring(jwtTokenUtil.getHeader().length());
        }
        try {
            if (!StringUtils.isEmpty(authToken)) {
                String username = jwtTokenUtil.getUsernameFromToken(authToken);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    // 将用户信息存入 authentication，方便后续校验
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        catch (Exception ex) {

        }
        chain.doFilter(request, response);
    }
}
