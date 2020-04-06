package com.demo.ncpdata.service;

import com.demo.ncpdata.security.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author sczhaoqi
 * @date 2020/4/6 11:49
 */
@Service
public class UserServiceImpl
    implements UserDetailsService
{
    @Override
    public UserDetails loadUserByUsername(String s)
            throws UsernameNotFoundException
    {
        return new User(s);
    }
}
