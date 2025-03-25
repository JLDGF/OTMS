package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Result;
import com.zjz.pojo.Users;


import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
public interface IUsersService extends IService<Users> {

    /**
     * 根据用户名查找
     * @param userName
     * @return
     */
    Integer selectIdByUserName(String userName);

    /*
    注册
     */
    void register(Users user);

    /*
    登录
     */
    Result login(Users user);

    Users selectByUsername(String userName);

    Users getUserCurrent();


    //管理员用户功能
    Result adminAddUser(Users user);

    Result adminDeletedUser(Integer userId);

    Result adminUpdateUser(Map<String, Object> userMap);


    Result adminGetAllUser(Map<String, Object> request);

    Result getUserDetalisById(Integer userId);

    Result adminGetUserRole(LocalDateTime startTime, LocalDateTime endTime);

    Result adminGetNewUser(LocalDateTime startTime, LocalDateTime endTime);
}
