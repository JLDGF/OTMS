package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.pojo.Parents;
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
public interface IParentsService extends IService<Parents> {

    //注册
    void register(Parents parent);

    //更新
    boolean updateById(Parents parent);

    //通过用户名获取数据
    Parents getByUsername(String username);

    //通过用户名获取数据
    Integer getStudentIdByUsername(String username);

    //更新信息
    String updateUserInfo(Map<String, Object> params);
}
