package com.zjz.onlinetutoringmanagementsystem.controller.teacher;


import com.zjz.onlinetutoringmanagementsystem.mapper.TeachercoursemappingMapper;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.ICourseService;
import com.zjz.onlinetutoringmanagementsystem.service.ITeachersService;
import com.zjz.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zjz
 * @since 2025-1-05
 */

@Slf4j
@RestController
public class CourseController {

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ITeachersService teachersService;

    @Autowired
    private TeachercoursemappingMapper teachercoursemappingMapper;

    //教师分页查询课程列表
    @GetMapping("/teachers/getMyCoursePage")
    public ToPage<Course> TeachetMyCoursePage(PageQuery query) throws Exception {
        return courseService.queryCoursePageByTeacherUsername(query);
    }
    //学生和家长分页查询课程列表
    @GetMapping("/course/getMyCoursePage")
    public ToPage<Course> StudentAndParentMyCoursePage(PageQuery query) throws Exception {
        return courseService.queryCoursePageByStudentId(query);
    }
    //学生和家长查询课程名录
    @GetMapping("/course/getMyCourseList")
    public Result UserGetMyCourseList() throws Exception {
        return courseService.GetMyCourseList();
    }

    //获取课程详情
    @GetMapping("/course/getCourseDetailsCheck")
    public Result getCourseDetailsCheck(Integer courseId) throws Exception {
        return courseService.getCourseDetailsCheck(courseId);
    }

    //新增课程
    @PostMapping("/teachers/addCourse")
    public Result AddCourse(@RequestBody Course course) throws Exception {
        if (course.getCourseId()!=null){
            return Result.error("非法输入");
        }

        if (courseService.insertCourse(course)){
            Teachercoursemapping tcmapping = new Teachercoursemapping();
            tcmapping.setTeacherId(course.getTeacherId());
            tcmapping.setCourseId(course.getCourseId());
            teachercoursemappingMapper.insert(tcmapping);

            return Result.success("新增成功");
        }else {
            return Result.error("新增失败，请检查信息是否合规");
        }
    }

    //删除课程
    @DeleteMapping("/teachers/deleteCourseById")
    public Result DeleteCourseById(Integer courseId) throws Exception {
        log.info("delete course now , courseId:{}",courseId);
        if(courseService.deleteCourseById(courseId)){
            return Result.success();
        }else {
            return Result.error("删除失败");
        }
    }

    //更新课程
    @PutMapping("/teachers/updateCourseById")
    public Result UpdateCourseById(@RequestBody Course course) throws Exception {

        if (courseService.updateCourseById(course)){
            return Result.success();
        }else {
            return Result.error("false");
        }
    }

    //查询已报名课程的学生
    @GetMapping("/teachers/getStudentByMyCourse")
    public Result getStudentByMyCourse(Integer courseId) throws Exception{
        log.info("get student list now");
        return courseService.getStudentByMyCourse(courseId);
    }
}
