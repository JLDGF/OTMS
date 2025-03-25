package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.zjz.pojo.Teachers;
import com.zjz.onlinetutoringmanagementsystem.mapper.TeachersMapper;
import com.zjz.pojo.Users;
import com.zjz.onlinetutoringmanagementsystem.service.ITeachersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class TeachersServiceImpl extends ServiceImpl<TeachersMapper, Teachers> implements ITeachersService {
    @Autowired
    TeachersMapper teachersMapper;

    @Override
    public void register(Teachers teacher) {
        teacher.setRegistrationDate(LocalDate.now());
        teachersMapper.insert(teacher);
    }

    @Override
    public Teachers getByUsername(String username) {
       return teachersMapper.getByUserName(username);
    }

    @Override
    public String updateUserInfo(Map<String, Object> params) {
        Teachers teacher = new Teachers();
        //注入
        teacher.setTeacherUserId((Integer) params.get("teacherUserId"));
        teacher.setTeacherName((String) params.get("teacherName"));
        teacher.setAge((Integer) params.get("age"));
        teacher.setProfile((String) params.get("profile"));
        teacher.setContactInfo((String) params.get("contactInfo"));

        teachersMapper.updateById(teacher);
        return "success";
    }

    @Override
    public Teachers getCourseTeacherInfo(Integer teacherId) {
        Teachers teacher = teachersMapper.selectById(teacherId);
        Teachers t = new Teachers();
        t.setTeacherUserId(teacherId);
        t.setTeacherUsername(teacher.getTeacherUsername());
        t.setTeacherName(teacher.getTeacherName());
        t.setRating(teacher.getRating());
        t.setEducationInfo(teacher.getEducationInfo());
        t.setOverallRating(teacher.getOverallRating());

        return t;
    }


}
