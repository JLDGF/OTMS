package com.zjz.onlinetutoringmanagementsystem.controller.allUser;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.*;
import com.zjz.pojo.*;
import com.zjz.utils.FileStorageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/public")
public class publicController {

    @Autowired
    private ICourseService courseService;

    @Autowired
    private IParentsService parentsService;

    @Autowired
    private ITeachersService teachersService;

    @Autowired
    private IStudentsService studentsService;

    @Autowired
    private INoticeService noticeService;

    @Autowired
    private IFileInfoService fileInfoService;

    @Autowired
    private IFaqService faqService;

    //分页查询课程
    @GetMapping("/getCoursePage")
    public ToPage<Course> AllCoursePage(PageQuery query, String courseName, String subject) throws Exception {

        return courseService.queryCoursePage(query,courseName,subject);
    }

    //课程单项查询
    @GetMapping("/getCourseById")
    public Result getCourse(@RequestParam Integer courseId) {
        try {
            Course course = courseService.getCourseWithStatusUpdate(courseId);
            return Result.success(course);
        } catch (Exception e) {
            log.error("获取课程失败", e);
            return Result.error("课程不存在");
        }
    }

    //课程页获取授课教师信息
    @GetMapping("/getCourseTeacherInfo")
    public Teachers getCourseTeacherInfo(Integer teacherId) throws Exception {
        //todo 条件？
        return teachersService.getCourseTeacherInfo(teacherId);
    }

    //家长单项查询
    @GetMapping("/getParentById")
    public Parents GetParent(Integer parentId) throws Exception {

        return parentsService.getById(parentId);
    }

    //教师单项查询
    @GetMapping("/getTeacherById")
    public Teachers GetTeacher(Integer teacherId) throws Exception {

        return teachersService.getById(teacherId);
    }

    //学生单项查询
    @GetMapping("/getStudentById")
    public Students GetStudent(Integer studentId) throws Exception {

        return studentsService.getById(studentId);
    }

    //文件下载
    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(Integer fileId) {
        FileInfo fileInfo = fileInfoService.getById(fileId);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return FileStorageUtils.downloadFileByPathAndName(fileInfo.getFilePath(), fileInfo.getFileName());
    }

    //获取公告列表
    @GetMapping("/getAllNotice")
    public Result userGetAllNotice() {
        return noticeService.userGetAllNotice();
    }

    //根据ID获取公告
    @GetMapping("/getNoticeById")
    public Result getNoticeById(Integer noticeId) {
        if (noticeService.getById(noticeId).getAnnouncementStatus()) {
            return Result.success(noticeService.getById(noticeId));
        } else {
            return Result.error("非法参数");
        }
    }

    @GetMapping("/AllFaqPage")
    public ToPage<Faq> adminGetFaqPage(PageQuery query,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String status) {
        log.info("FAQ分页查询：页码{}，页大小{}", query.getPageNo(), query.getPageSize());
        return faqService.userGetFaqPage(query, keyword, status);
    }

}
