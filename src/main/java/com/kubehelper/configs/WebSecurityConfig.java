/*
Kube Helper
Copyright (C) 2021 JDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kubehelper.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * https://github.com/zkoss/zkspringboot/tree/master/zkspringboot-demos/zkspringboot-security-demo
 *
 * @author JDev
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String ZUL_FILES = "/zkau/web/**/*.zul";
    private static final String[] ZK_RESOURCES = {
            "/zkau/web/**/js/**",
            "/zkau/web/**/zul/css/**",
            "/zkau/web/**/font/**",
            "/zkau/web/**/img/**"
    };
    // allow desktop cleanup after logout or when reloading login page
    private static final String REMOVE_DESKTOP_REGEX = "/zkau\\?dtid=.*&cmd_0=rmDesktop&.*";

    @Autowired
    private KubeHelperUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ZK already sends a AJAX request with a built-in CSRF token,
        // please refer to https://www.zkoss.org/wiki/ZK%20Developer's%20Reference/Security%20Tips/Cross-site%20Request%20Forgery
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers(ZUL_FILES).denyAll() // block direct access to zul files
                .antMatchers(HttpMethod.GET, ZK_RESOURCES).permitAll() // allow zk resources
                .regexMatchers(HttpMethod.GET, REMOVE_DESKTOP_REGEX).permitAll() // allow desktop cleanup
                .requestMatchers(req -> "rmDesktop".equals(req.getParameter("cmd_0"))).permitAll() // allow desktop cleanup from ZATS
                .mvcMatchers("/", "/home", "/logout").permitAll()
                .mvcMatchers("/kubehelper").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/home").defaultSuccessUrl("/kubehelper")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/home")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth){
        auth.authenticationProvider(authenticationProvider());
    }


    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}