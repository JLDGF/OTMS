package com.zjz.onlinetutoringmanagementsystem.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Students;


import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
public interface IStudentsService extends IService<Students> {

    //注册
    void register(Students student);

    //通过用户名获取信息
    Students getByUsername(String username);

    //更新信息
    String updateUserInfo(Map<String, Object> params);
}
