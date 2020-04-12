package com.jdpgrailsdev.oasis.timeline.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/status/check").permitAll();
        super.configure(http);
    }
}
