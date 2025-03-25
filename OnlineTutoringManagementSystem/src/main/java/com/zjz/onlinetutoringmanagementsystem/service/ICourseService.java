package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.pojo.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-01-04
 */
public interface ICourseService extends IService<Course> {

    //分页查询课程
    ToPage<Course> queryCoursePage(PageQuery query,String courseName,String subject);

    //根据教师用户名分页查询该用户课程
    ToPage<Course> queryCoursePageByTeacherUsername(PageQuery query) throws Exception;

    //根据学生用户名分页查询该用户课程
    ToPage<Course> queryCoursePageByStudentId(PageQuery query) throws Exception;

    //获取课程详情
    Result getCourseDetailsCheck(Integer courseId) throws Exception;

    //新增课程
    Boolean insertCourse(Course course) throws Exception;

    //删除课程
    Boolean deleteCourseById(Integer id) throws Exception;

    //更新课程
    Boolean updateCourseById(Course course) throws Exception;

    //查询某课程已报名学生
    Result getStudentByMyCourse(Integer courseId);

    //学生和家长查询课程名录
    Result GetMyCourseList() throws Exception;

    Course getCourseWithStatusUpdate(Integer courseId);

    ToPage<Course> adminGetCoursePage(PageQuery query, String keyword, String status, String registeredStudents, Integer teacherId, LocalDateTime startTime, LocalDateTime endTime, String subject);
}
