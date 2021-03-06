package com.bfchengnuo.security.browser;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

/**
 * 自定义处理用户登陆的逻辑
 *
 * 实现用户信息的获取应该是业务系统，不应该放在 browser 模块下，可参考 demo 模块下
 *
 * @author Created by 冰封承諾Andy on 2019/7/15.
 */
// @Component("myUserDetailsService")
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService, SocialUserDetailsService {
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // BCryptPasswordEncoder 每次生成的会不一样，应该在注册的时候保存，这里直接拿数据库保存的
        String pwd = passwordEncoder.encode("123123");
        System.out.println("PWD：" + pwd);
        // 简单实现
        return new User(username, pwd,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        return null;
    }
}
