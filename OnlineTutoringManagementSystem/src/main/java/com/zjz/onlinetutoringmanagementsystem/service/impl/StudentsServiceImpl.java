package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.zjz.pojo.Parents;
import com.zjz.pojo.Students;
import com.zjz.onlinetutoringmanagementsystem.mapper.StudentsMapper;
import com.zjz.pojo.Users;
import com.zjz.onlinetutoringmanagementsystem.service.IStudentsService;
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
public class StudentsServiceImpl extends ServiceImpl<StudentsMapper, Students> implements IStudentsService {
    @Autowired
    StudentsMapper studentsMapper;

    @Override
    public void register(Students student) {
        student.setRegistrationDate(LocalDate.now());
        studentsMapper.insert(student);
    }

    @Override
    public Students getByUsername(String username) {
        return studentsMapper.getByUsername(username);
    }

    @Override
    public String updateUserInfo(Map<String, Object> params) {
        Students student = new Students();

        //注入
        student.setStudentUserId((Integer) params.get("studentUserId"));
        student.setStudentName((String) params.get("studentName"));
        student.setProfile((String) params.get("profile"));
        student.setContactInfo((String) params.get("contactInfo"));

        studentsMapper.updateById(student);
        return "success";
    }
}
