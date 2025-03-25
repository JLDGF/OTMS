package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.enums.CourseStatus;
import com.zjz.onlinetutoringmanagementsystem.mapper.StudentcoursemappingMapper;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.*;
import com.zjz.pojo.*;
import com.zjz.onlinetutoringmanagementsystem.mapper.EnrollmentMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-01-10
 */
@Slf4j
@Service
public class EnrollmentServiceImpl extends ServiceImpl<EnrollmentMapper, Enrollment> implements IEnrollmentService {

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Autowired
    private StudentcoursemappingMapper studentcoursemappingMapper;

    @Autowired
    private IUsersService usersService;

    @Autowired
    private IParentsService parentsService;

    @Autowired
    private ITeachersService teachersService;

    @Autowired
    private ICourseService courseService;

    @Override
    public Boolean ParentEnroll(Integer courseId , Integer teacherId) throws Exception {

        try {//构造报名信息后插入
            Enrollment enrollment = new Enrollment();
            enrollment.setCourseId(courseId);
            //获取学生ID
            Integer studentId = parentsService.getStudentIdByUsername(SecurityUtils.getCurrentUsername());

            //验证学生是否已报名该课程
            if(enrollCheck(courseId,studentId)){
                throw new Exception("已报名");
            }
            if(courseService.getById(courseId).getCourseStatus()!= CourseStatus.IN_PROGRESS){
                throw new Exception("课程已结束");
            }
            if(courseService.getById(courseId).getMaxStudents().equals(courseService.getById(courseId).getRegisteredStudents())){
                throw new Exception("课程已满员");
            }
            if(studentId == null){
                throw new Exception("请在绑定学生后报名");
            }

            enrollment.setStudentId(studentId);
            enrollment.setEnrollmentTime(LocalDateTime.now());
            enrollment.setTeacherId(teacherId);
            enrollmentMapper.insert(enrollment);

        }catch (Exception e){
            log.info("error:");
            throw e;
        }
        return true;
    }

    //教师分页查询报名申请
    @Override
    public ToPage<Enrollment> queryEnrollmentPageByUsername(PageQuery query) throws Exception {
        log.info("queryEnrollmentPage Now");
        // 1. 条件构造
        Page<Enrollment> page = query.toMpPage();  // 将 PageQuery 转换为 MyBatis-Plus Page 对象
        QueryWrapper<Enrollment> queryWrapper = new QueryWrapper<>();  // 创建查询条件包装器
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        Users user = usersService.selectByUsername(username);
        log.info("username:{}", username);

        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new Exception("审核获取异常，请联系管理员");
        }
        //2.将id注入查询条件
        switch (role) {
            case "ROLE_teacher":
                queryWrapper.eq("teacher_id", user.getUserId());  // 精确查询教师ID

                break;
            case "ROLE_student":
                queryWrapper.eq("student_id", user.getUserId());  // 精确查询学生ID

                break;
            case "ROLE_parent":
                queryWrapper.eq("student_id", parentsService.getStudentIdByUsername(username));  // 精确查询学生ID

                break;
        }

        // 3. 查询，传入分页和查询条件
        page(page, queryWrapper);
        // 4. 返回数据
        return ToPage.of(page, Enrollment.class);
    }

    @Override
    public Result agree(Integer enrollmentId) {
        Enrollment e = enrollmentMapper.selectById(enrollmentId);
        //检查是否已到最大人数
        Course course = courseService.getById(e.getCourseId());

        if (course.getRegisteredStudents()+1 > course.getMaxStudents()){
            return Result.error("课程人数已满");
        }

        e.setAcceptanceStatus(1);
        //1.更改审核表中该项状态
        enrollmentMapper.updateById(e);
        //2.加入学生-课程对应表
        studentcoursemappingMapper.insertNewMapping(e.getStudentId(),e.getCourseId());
        //3.更新课程已加入人数

        course.setRegisteredStudents(course.getRegisteredStudents()+1);
        courseService.updateById(course);
        //4.返回结果
        return Result.success("已接受报名");
    }

    @Override
    public Result reject(Integer enrollmentId) {
        Enrollment e = enrollmentMapper.selectById(enrollmentId);
        e.setAcceptanceStatus(2);
        //1.更改审核表中该项状态
        enrollmentMapper.updateById(e);
        //2.返回结果
        return Result.success("已拒绝报名");
    }


    //验证学生是否已报名该课程
    private Boolean enrollCheck(Integer courseId, Integer studentId) {
        QueryWrapper<Enrollment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId)
                .eq("student_id", studentId);

        // 如果有查到记录，则返回 true 表示不可以报名
        return enrollmentMapper.exists(queryWrapper);
    }

}

