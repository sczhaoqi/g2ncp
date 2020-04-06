package com.demo.ncpdata.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;

/**
 * @author sczhaoqi
 * @date 2020/4/5 0:34
 */
@Configuration
public class SecurityConfig
        extends WebSecurityConfigurerAdapter
        implements WebMvcConfigurer
{
    @RequestMapping("/user")
    public Principal user(Principal principal)
    {
        return principal;
    }

    @Override
    protected void configure(HttpSecurity http)
            throws Exception
    {
        http.antMatcher("/**")
                .authorizeRequests().antMatchers("/login**", "/webjars/**", "/error**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .addFilterAt(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        http.exceptionHandling()
                .accessDeniedHandler(userAccessDeniedEntryPointHandler)
                .authenticationEntryPoint(userAccessDeniedEntryPointHandler);
    }

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private UserAccessDeniedEntryPointHandler userAccessDeniedEntryPointHandler;

}
