package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.zjz.pojo.Parents;
import com.zjz.onlinetutoringmanagementsystem.mapper.ParentsMapper;
import com.zjz.onlinetutoringmanagementsystem.service.IParentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
@Service
public class ParentsServiceImpl extends ServiceImpl<ParentsMapper, Parents> implements IParentsService {
    @Autowired
    ParentsMapper parentsMapper;

    //注册
    @Override
    public void register(Parents parent) {
        parent.setRegistrationDate(LocalDate.now());
        parentsMapper.insert(parent);
    }

    //更新
    @Override
    public boolean updateById(Parents parent) {
        parentsMapper.updateById(parent);
        return false;
    }

    @Override
    public Parents getByUsername(String username) {
        return parentsMapper.getByUsername(username);
    }

    @Override
    public Integer getStudentIdByUsername(String username) {
        return parentsMapper.getStudentIdByUsername(username);
    }

    @Override
    public String updateUserInfo(Map<String, Object> params) {
        Parents parent = new Parents();
        //注入
        parent.setParentUserId((Integer) params.get("parentUserId"));
        parent.setParentName((String) params.get("parentName"));
        parent.setProfile((String) params.get("profile"));
        parent.setContactInfo((String) params.get("contactInfo"));

        parentsMapper.updateById(parent);
        return "success";
    }
}
