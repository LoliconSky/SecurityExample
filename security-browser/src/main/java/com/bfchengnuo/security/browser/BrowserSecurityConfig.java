package com.bfchengnuo.security.browser;

import com.bfchengnuo.security.core.authentication.AbstractChannelSecurityConfig;
import com.bfchengnuo.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.bfchengnuo.security.core.authorize.AuthorizeConfigManager;
import com.bfchengnuo.security.core.properties.SecurityProperties;
import com.bfchengnuo.security.core.validate.code.ValidateCodeSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * SpringSecurity 浏览器配置
 *
 * 自定义认证规则可以将 {@link UserDetailsService} 的实现注入到 IOC，
 * 或者通过继承 {@link WebSecurityConfigurerAdapter#configure(AuthenticationManagerBuilder)} 的重载进行配置
 *
 * @author Created by 冰封承諾Andy on 2019/7/15.
 */
@Configuration
public class BrowserSecurityConfig extends AbstractChannelSecurityConfig {
    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private DataSource dataSource;

    @Qualifier("myUserDetailsService")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Autowired
    private ValidateCodeSecurityConfig validateCodeSecurityConfig;

    @Autowired
    private InvalidSessionStrategy invalidSessionStrategy;

    @Autowired
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private AuthorizeConfigManager authorizeConfigManager;

    /**
     * 社交第三方登陆的配置类
     */
    @Autowired
    private SpringSocialConfigurer springSocialConfigurer;


    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        // 基于 JDBC 的 “记住我” 实现
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        // jdbcTokenRepository.setCreateTableOnStartup(true);

        return jdbcTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 使公共配置生效
        super.applyPasswordAuthenticationConfig(http);

        http.apply(validateCodeSecurityConfig)
                .and()
                .apply(smsCodeAuthenticationSecurityConfig)
                .and()
                .apply(springSocialConfigurer)
                .and()
                // 记住我 配置
                .rememberMe()
                    .tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
                    // 自定义密码处理
                    .userDetailsService(userDetailsService)
                    .and()
                .sessionManagement()
                    // session 失效后跳转逻辑
                    .invalidSessionStrategy(invalidSessionStrategy)
                    // 用户同时在线数量
                    .maximumSessions(securityProperties.getBrowser().getSession().getMaximumSessions())
                    // 是否阻止后来的登陆行为
                    .maxSessionsPreventsLogin(securityProperties.getBrowser().getSession().isMaxSessionsPreventsLogin())
                    // session 并发的处理逻辑
                    .expiredSessionStrategy(sessionInformationExpiredStrategy)
                    .and()
                    .and()
                .logout()
                    // 退出的请求地址，默认就是 signOut
                    .logoutUrl("/signOut")
                    .logoutSuccessHandler(logoutSuccessHandler)
                    .deleteCookies("JSESSIONID")
                    .and()
                .csrf().disable();

        // 设置权限
        authorizeConfigManager.config(http.authorizeRequests());
    }

    // @Override
    // protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //     //使用自定义认证规则，并且使用 BCrypt 算法处理密码
    //     auth.userDetailsService(myUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    // }
}
