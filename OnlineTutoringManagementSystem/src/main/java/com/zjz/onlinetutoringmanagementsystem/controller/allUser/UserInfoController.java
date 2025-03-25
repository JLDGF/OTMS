package com.zjz.onlinetutoringmanagementsystem.controller.allUser;

import com.zjz.pojo.Result;
import com.zjz.onlinetutoringmanagementsystem.service.IParentsService;
import com.zjz.onlinetutoringmanagementsystem.service.IStudentsService;
import com.zjz.onlinetutoringmanagementsystem.service.ITeachersService;
import com.zjz.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    @Autowired
    IParentsService parentsService;

    @Autowired
    IStudentsService studentsService;

    @Autowired
    ITeachersService teachersService;

    @GetMapping
    public Result userInfo(String username) {
        log.info("userInfo start");

        String role = SecurityUtils.getCurrentUserRole();

        //todo 查询前更新总体评分（总评分/活动次数）

        if ("ROLE_teacher".equals(role)) {
            // 如果是教师角色，查询教师表
            return Result.success(teachersService.getByUsername(username));
        } else if ("ROLE_student".equals(role)) {
            // 如果是学生角色，查询学生表
            return Result.success(studentsService.getByUsername(username));
        } else if ("ROLE_parent".equals(role)) {
            // 如果是学生角色，查询学生表
            return Result.success(parentsService.getByUsername(username));
        } else {
            return Result.error("异常");
        }
    }
    @PutMapping("/updateInfo")
    public Result updateInfo(@RequestBody Map<String, Object> params) {
        log.info("user update Info Now");

        String role = SecurityUtils.getCurrentUserRole();

        if ("ROLE_teacher".equals(role)) {
            // 如果是教师角色，查询教师表
            return Result.success(teachersService.updateUserInfo(params));
        }
        else if ("ROLE_student".equals(role)) {
            // 如果是学生角色，查询学生表
            return Result.success(studentsService.updateUserInfo(params));
        } else if ("ROLE_parent".equals(role)) {
            // 如果是学生角色，查询学生表
            return Result.success(parentsService.updateUserInfo(params));
        } else {
            return Result.error("异常");
        }

    }

}
