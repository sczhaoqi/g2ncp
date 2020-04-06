package com.demo.ncpdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableOAuth2Client
@SpringBootApplication
public class DemoApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(DemoApplication.class, args);
    }
}
