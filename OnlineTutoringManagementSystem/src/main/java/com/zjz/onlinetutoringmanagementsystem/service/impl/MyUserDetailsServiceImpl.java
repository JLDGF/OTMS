package com.zjz.onlinetutoringmanagementsystem.service.impl;


import com.zjz.onlinetutoringmanagementsystem.mapper.UsersMapper;
import com.zjz.pojo.CustomerUserDetails;
import com.zjz.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersMapper usersMapper;  // 你需要一个 service 来获取用户信息

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中查询用户信息
        Users user = usersMapper.selectByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // 返回自定义的 UserDetails
        return new CustomerUserDetails(user);
    }
}
