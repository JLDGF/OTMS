package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.pojo.Teachers;

import com.baomidou.mybatisplus.extension.service.IService;


import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
public interface ITeachersService extends IService<Teachers> {

    //注册
    void register(Teachers teacher);

    //根据用户名获取用户信息
    Teachers getByUsername(String username);

    //更新信息
    String updateUserInfo(Map<String, Object> params);

    Teachers getCourseTeacherInfo(Integer teacherId);
}
