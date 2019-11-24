package com.bfchengnuo.security.demo.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 示例对象，用于 UserDetailsService 返回，判断权限
 *
 * @author 冰封承諾Andy
 * @date 2019-11-22
 */
public class Admin implements UserDetails {
    /**
     * 数据库主键
     */
    private Long id;
    /**
     * 审计日志，记录条目创建时间，自动赋值，不需要程序员手工赋值
     * JPA 使用 @Temporal(TemporalType.TIMESTAMP) 和 @CreatedDate
     */
    private Date createdTime;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户的所有角色
     */
    private Set<RoleAdmin> roles = new HashSet<>();
    /**
     * 用户有权访问的所有url，不持久化到数据库
     * JPA ： @Transient
     */
    private Set<String> urls = new HashSet<>();
    /**
     * 用户有权的所有资源id，不持久化到数据库
     */
    private Set<Long> resourceIds = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public Set<Long> getAllResourceIds() {
        init(resourceIds);
        forEachResource(resource -> resourceIds.add(resource.getId()));
        return resourceIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }


    public Set<RoleAdmin> getRoles() {
        return roles;
    }


    public void setRoles(Set<RoleAdmin> roles) {
        this.roles = roles;
    }

    public Set<String> getUrls() {
        init(urls);
        forEachResource(resource -> urls.addAll(resource.getUrls()));
        return urls;
    }

    private void init(Set<?> data){
        if (CollectionUtils.isEmpty(data)) {
            if (data == null) {
                data = new HashSet<>();
            }
        }
    }

    private void forEachResource(Consumer<Resource> consumer) {
        for (RoleAdmin role : roles) {
            for (RoleResource resource : role.getRole().getResources()) {
                consumer.accept(resource.getResource());
            }
        }
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }
}