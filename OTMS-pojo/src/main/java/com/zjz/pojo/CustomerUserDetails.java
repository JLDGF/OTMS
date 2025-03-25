package com.zjz.pojo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.zjz.enums.UserRole;

import java.util.Collection;
import java.util.Collections;


public class CustomerUserDetails implements UserDetails {

    private Integer userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;

    // 构造方法
    public CustomerUserDetails(Users user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.getEnabled() != null && user.getEnabled(); // 如果enabled字段为null，默认为false

        // 角色转换为 GrantedAuthority
        if (user.getRole() != null) {
            this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        } else {
            this.authorities = Collections.emptyList();
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 默认不考虑过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 默认不考虑锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 默认不考虑凭证过期
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // 提供其他getter，如果需要
    public Integer getUserId() {
        return userId;
    }
}
