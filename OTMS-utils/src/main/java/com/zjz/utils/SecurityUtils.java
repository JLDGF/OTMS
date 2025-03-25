package com.zjz.utils;

import com.zjz.pojo.CustomerUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Slf4j
public class SecurityUtils {

    // 获取当前用户角色（假设角色是通过 GrantedAuthority 设置的）
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                for (GrantedAuthority authority : authorities) {
                    return authority.getAuthority(); // 返回第一个角色
                }
            }
        }
        return null; // 如果没有获取到角色，返回 null
    }

    // 获取当前用户名
    public static String getCurrentUsername() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomerUserDetails) {
            CustomerUserDetails userDetails = (CustomerUserDetails) principal;
            String username = userDetails.getUsername();  // 获取用户名或其他字段
            log.info("Username: {}", username);
            return username;
        } else if (principal instanceof String) {
            String username = (String) principal;
            log.info("Username: {}", username);
            return username;
        }
        return "error now";

    }


}
