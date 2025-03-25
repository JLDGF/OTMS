package com.zjz.onlinetutoringmanagementsystem.controller.allUser;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjz.enums.ApprovalsStatus;
import com.zjz.enums.AssignmentStatus;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.*;
import com.zjz.pojo.*;
import com.zjz.utils.FileStorageUtils;
import com.zjz.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UsersController {
    @Autowired
    private IUsersService usersService;

    @Autowired
    private IStudentsService studentsService;

    @Autowired
    private ITeachersService teachersService;

    @Autowired
    private IParentsService parentsService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private IEnrollmentService enrollmentService;

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IAssignmentService assignmentService;

    @Autowired
    private IFileInfoService fileInfoService;

    @Autowired
    private IAvatarService avatarService;

    @Autowired
    private IApprovalsService approvalsService;

    @Autowired
    private IFeeTransactionsService feeTransactionsService;

    @Autowired
    private IFaqService faqService;

    @Autowired
    private IAssignmentSubmissionsService assignmentSubmissionsService;

    @GetMapping("/current")
    public Result getUserCurrent() {
        return Result.success(usersService.getUserCurrent());
    }

    @GetMapping("/getUsernameById")
    public String getUsernameById(Integer userId) {
        return usersService.getById(userId).getUsername();
    }

    //根据ID获取其他用户信息
    @GetMapping("/getOtherUserInfoById")
    public Result getOtherUserInfoById(Integer userId) {
        try {
            log.info("获取用户详细信息，用户ID：{}", userId);

            // 1. 查询基础用户信息
            Users user = usersService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 2. 创建通用Map存储信息
            Map<String, Object> resultMap = new HashMap<>();

            // 3. 填充公共字段
            resultMap.put("userId", user.getUserId());
            resultMap.put("username", user.getUsername());
            resultMap.put("role", user.getRole().getDesc());

            // 4. 根据角色查询详细信息
            switch (user.getRole()) {
                case student:
                    Students student = studentsService.getById(userId);
                    if (student != null) {
                        resultMap.put("studentUserId", student.getStudentUserId());
                        resultMap.put("parentUserId", student.getParentUserId());
                        resultMap.put("StudentUsername", student.getStudentUsername());
                        resultMap.put("grade", student.getGrade());
                        resultMap.put("registrationDate", student.getRegistrationDate());
                        resultMap.put("contactInfo", student.getContactInfo());
                        resultMap.put("OverallRating", student.getOverallRating());
                        resultMap.put("profile", student.getProfile());
                    }
                    break;

                case teacher:
                    Teachers teacher = teachersService.getById(userId);
                    if (teacher != null) {
                        resultMap.put("teacherUserId", teacher.getTeacherUserId());
                        resultMap.put("teacherName", teacher.getTeacherName());
                        resultMap.put("age", teacher.getAge());
                        resultMap.put("educationInfo", teacher.getEducationInfo());
                        resultMap.put("registrationDate", teacher.getRegistrationDate());
                        resultMap.put("contactInfo", teacher.getContactInfo());
                        resultMap.put("activityTime", teacher.getActivityTime());
                        resultMap.put("totalPoints", teacher.getTotalPoints());
                        resultMap.put("OverallRating", teacher.getOverallRating());
                        resultMap.put("profile", teacher.getProfile());
                    }
                    break;

                case parent:
                    Parents parent = parentsService.getById(userId);
                    if (parent != null) {
                        resultMap.put("parentUserId", parent.getParentUserId());
                        resultMap.put("parentUsername", parent.getParentUsername());
                        resultMap.put("parentName", parent.getParentName());
                        resultMap.put("studentUserId", parent.getStudentUserId());
                        resultMap.put("registrationDate", parent.getRegistrationDate());
                        resultMap.put("contactInfo", parent.getContactInfo());
                        resultMap.put("profile", parent.getProfile());
                    }
                    break;
                default:
                    // 非标准角色只返回基础信息
                    log.warn("未知用户角色: {}", user.getRole());
                    break;
            }
            return Result.success(resultMap);
        } catch (Exception e) {
            log.error("获取用户信息失败: ", e);
            return Result.error("获取信息失败");
        }
    }

    //用户分页查询报名申请
    @GetMapping("/getMyEnrollPage")
    public Object MyEnrollPage(PageQuery query) throws Exception {

        log.info("user Enroll Page search");
        //todo 条件？
        ToPage<Enrollment> t = enrollmentService.queryEnrollmentPageByUsername(query);

        List<Enrollment> list = t.getList();
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (Enrollment enrollment : list) {
            // 创建一个 Map 来存储 Enrollment 的部分信息
            Map<String, Object> map = new HashMap<>();
            map.put("enrollmentId", enrollment.getEnrollmentId());
            map.put("courseId", enrollment.getCourseId());
            //插入课程名
            String courseName = courseService.getById(enrollment.getCourseId()).getCourseName();
            map.put("courseName", courseName);
            map.put("studentId", enrollment.getStudentId());
            //插入学生姓名和总体分数
            String studentName = studentsService.getById(enrollment.getStudentId()).getStudentName();
            Float studentOverallRating = studentsService.getById(enrollment.getStudentId()).getOverallRating();

            map.put("studentName", studentName);
            map.put("studentOverallRating", studentOverallRating);
            map.put("acceptanceStatus", enrollment.getAcceptanceStatus());

            mapList.add(map);
        }
        // 封装返回
        return new ToPage<>(t.getTotal(), t.getPages(), mapList);
    }

    //用户分页查询已完成活动记录
    @GetMapping("/getMyCompletedActivityPage")
    public ToPage<Activity> MyCompletedActivityPage(PageQuery query) throws Exception {
        log.info("user Completed Activity Page search");

        return activityService.queryCompletedActivityPageByUsername(query);
    }

    //用户分页查询未开始活动记录
    @GetMapping("/getMyUpcomingActivityPage")
    public ToPage<Activity> MyUpcomingActivityPage(PageQuery query) throws Exception {
        log.info("user Upcoming Activity Page search");

        return activityService.queryUpcomingActivityPageByUsername(query);
    }

    //用户分页查询已失效活动记录
    @GetMapping("/getMyInvalidActivityPage")
    public ToPage<Activity> MyInvalidActivityPage(PageQuery query) throws Exception {
        log.info("user Upcoming Activity Page search");

        return activityService.queryInvalidActivityPageByUsername(query);
    }

    //用户分页查询某课程活动记录
    @GetMapping("/getMyActivityPageByCourse")
    public ToPage<Activity> MyCompletedActivityPage(PageQuery query, Integer courseId) throws Exception {
        log.info("user Activity Page search By Course");

        return activityService.queryActivityPageByCourse(query, courseId);
    }

    //用户查询活动记录详细信息
    @GetMapping("/getActivityDetails")
    public Result ActivityDetail(Integer activityId) throws Exception {
        log.info("get Activity Details Now");

        return activityService.getActivityDetails(activityId);
    }

    //用户分页查询自己发布或参与的作业
    @GetMapping("/getMyAssignmentPage")
    public ToPage<Assignment> MyAssignmentPage(PageQuery query, AssignmentStatus assignmentStatus) {
        log.info("user Assignment Page search");

        return assignmentService.queryAssignmentPageByUserId(query, assignmentStatus);
    }

    //用户查询自己发布或参与的作业详情
    @GetMapping("/getAssignmentDetails")
    public Result GetAssignmentDetailsById(Integer assignmentId) {
        log.info("Get Assignment Details By Id");

        return assignmentService.getAssignmentDetailsById(assignmentId);
    }

    //根据文件ID列表获取文件详情
    @GetMapping("/GetFilesDetailsByIdList")
    public Result GetFilesDetailsByIdList(@RequestParam("idList") List<Integer> idList) {
        log.info("Get Files Details By IdList");
        return fileInfoService.GetFilesDetailsByIdList(idList);
    }

    //头像上传
    @PostMapping("avatarUpload")
    public Result AvatarUpload(MultipartFile avatar) {
        try {
            avatarService.avatarUpload(avatar);
        } catch (Exception e) {
            log.info("error:{}", e.getMessage());
            return Result.error("上传失败");
        }

        return Result.success();
    }

    //获取头像图片
    @GetMapping("getAvatar")
    public ResponseEntity<Resource> GetAvatar(Integer userId) {
        if (avatarService.getById(userId) == null) {
            return null;
        } else {
            return FileStorageUtils.downloadFileByPathAndName(avatarService.getById(userId).getAvatarPath(), "Avatar");
        }
    }

    //用户申请
    @PostMapping("approvals")
    public Result userApprovals(@RequestParam String content) {
        try {
            return approvalsService.userApprovals(content);
        } catch (Exception e) {
            return Result.error("申请失败");
        }
    }

    //获取账单
    @GetMapping("queryFeePageByQuery")
    public Result userGetFeeList(PageQuery query,String status){
        try {
            return feeTransactionsService.userGetFeeList(query,status);
        }catch (Exception e){
            log.error(e.toString());
            return Result.error("获取账单失败，请检查参数");
        }
    }

    //检查作业提交状态
    @GetMapping("CheckSubmit")
    public Result CheckSubmit(Integer assignmentId){
        log.info("Student Check Submit,assignmentId:{}",assignmentId);

        return assignmentSubmissionsService.CheckSubmit(assignmentId);
    }

    //获取已提交的信息
    @GetMapping("GetSubmission")
    public AssignmentSubmissions GetSubmission(Integer assignmentId){
        log.info("Student Check Submit,assignmentId:{}",assignmentId);

        return assignmentSubmissionsService.GetSubmission(assignmentId);
    }
}
