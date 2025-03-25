package com.zjz.onlinetutoringmanagementsystem.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjz.onlinetutoringmanagementsystem.mapper.StudentcoursemappingMapper;
import com.zjz.onlinetutoringmanagementsystem.mapper.TeachercoursemappingMapper;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IAssignmentService;
import com.zjz.onlinetutoringmanagementsystem.service.ICourseService;
import com.zjz.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/admin/course")
public class adminCourseController {

    @Autowired
    private ICourseService courseService;

    @Autowired
    private TeachercoursemappingMapper teachercoursemappingMapper;

    @Autowired
    private StudentcoursemappingMapper studentcoursemappingMapper;

    @Autowired
    private IAssignmentService assignmentService;

    // 分页获取课程列表
    @GetMapping("/AllCoursePage")
    public ToPage<Course> adminGetCoursePage(
            PageQuery query,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String RegisteredStudents,
            @RequestParam(required = false) Integer teacherId,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String subject
    ) {
        log.info("课程分页查询：页码{}，页大小{}", query.getPageNo(), query.getPageSize());
        return courseService.adminGetCoursePage(
                query, keyword, status, RegisteredStudents, teacherId, startTime, endTime, subject
        );
    }

    //分页获取所有报名该课程的学生
    @GetMapping("/getStudentsByCourseId")
    public Result adminGetStudentsByCourseId(Integer courseId) {
        try {
            return courseService.getStudentByMyCourse(courseId);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }

    // 新增课程
    @PostMapping("/add")
    public Result createCourse(@RequestBody Course course) throws Exception {
        try {
            if (course.getCourseId() != null) {
                return Result.error("非法输入");
            }

            if (courseService.insertCourse(course)) {
                Teachercoursemapping tcmapping = new Teachercoursemapping();
                tcmapping.setTeacherId(course.getTeacherId());
                tcmapping.setCourseId(course.getCourseId());
                teachercoursemappingMapper.insert(tcmapping);
            } else {
                return Result.error("未知错误，请检查数据格式");
            }
            log.info("新增课程：{}", course.getCourseName());

            return Result.success("课程创建成功");
        } catch (Exception e) {
            return Result.error(e.toString());
        }

    }

    // 更新课程
    @PutMapping("/update")
    public Result updateCourse(@RequestBody Course course) throws Exception {
        log.info("更新课程：ID{}", course.getCourseId());
        courseService.updateCourseById(course);
        return Result.success("课程更新成功");
    }

    // 删除课程
    @DeleteMapping("/delete")
    public Result deleteCourse(@RequestParam Integer courseId) throws Exception {
        log.info("delete course now , courseId:{}", courseId);
        if (courseService.deleteCourseById(courseId)) {

            //同时删除关联表
            QueryWrapper<Teachercoursemapping> queryWrappertc = new QueryWrapper<>();
            QueryWrapper<Studentcoursemapping> queryWrappersc = new QueryWrapper<>();

            queryWrappertc.eq("course_id", courseId);
            queryWrappersc.eq("course_id", courseId);

            teachercoursemappingMapper.delete(queryWrappertc);
            studentcoursemappingMapper.delete(queryWrappersc);

            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    // 分页获取某课程的作业列表
    @GetMapping("/GetAssignmentPageByCourseId")
    public ToPage<Assignment> adminGetAssignmentPageByCourseId(PageQuery query, Integer courseId) {
        try {
            return assignmentService.GetAssignmentPageByCourseId(query, courseId);
        }catch (Exception e){
            log.error(e.toString());
            throw e;
        }
    }

    //删除作业
    @DeleteMapping("deleteAssignment")
    private Result adminDeleteAssignmentByID(Integer assignmentId){
        try {
            assignmentService.removeById(assignmentId);
            return Result.success("删除成功");
        }catch (Exception e){
            log.error(e.toString());
            return Result.error(e.toString());
        }
    }

}