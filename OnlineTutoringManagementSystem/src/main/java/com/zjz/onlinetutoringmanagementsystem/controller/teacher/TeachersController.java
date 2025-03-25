package com.zjz.onlinetutoringmanagementsystem.controller.teacher;

import com.zjz.enums.GradeStatus;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.*;
import com.zjz.pojo.AssignmentSubmissions;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import com.zjz.utils.FileStorageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
@Slf4j
@RestController
@RequestMapping("/teachers")
public class TeachersController {
    @Autowired
    private IEnrollmentService enrollmentService;

    @Autowired
    private IAssignmentService assignmentService;

    @Autowired
    IAssignmentSubmissionsService assignmentSubmissionsService;

    @PutMapping("/auditEnroll")
    //用户报名审核
    public Result UserEnrollAudit(@RequestBody Map<String, Object> request){

        try {
            //获取ID和审核信息
            Integer enrollmentId = (Integer) request.get("enrollmentId");
            Boolean auditResult = Boolean.parseBoolean((String) request.get("auditResult"));
            log.info(auditResult.toString());
            //根据审核信息，改变审核状态，接受时更新学生-课程对应表
            if (auditResult){
                return enrollmentService.agree(enrollmentId);
            }else {
                return enrollmentService.reject(enrollmentId);
            }
        }catch (Exception e){
            log.info("error:{}",e);
            return Result.error("e");
        }
    }

    @PostMapping("/PostAssignment")
    public Result postAssignment(
            @RequestParam("assignmentName") String assignmentName,
            @RequestParam("assignmentRequirements") String assignmentRequirements,
            @RequestParam("studentIds") String studentIds,
            @RequestParam("endTime") String endTime,
            @RequestParam("startTime") String startTime,
            @RequestParam("courseId") Integer courseID,
            @RequestParam(value = "attachments",required = false) MultipartFile[] attachments
    ) {

        try {
            // 1. 处理学生ID
            List<Long> studentIdList = Arrays.stream(studentIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            // 2. 处理时间
            LocalDateTime deadline = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime beginTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            //没有附件时设置附件为null后跳过文件处理，直接下一步
            if (attachments==null){
                return assignmentService.createAssignment(courseID,assignmentName,assignmentRequirements,deadline,beginTime,studentIdList,null);
            }
            // 3. 处理文件上传
            List<MultipartFile> files = new ArrayList<>();
            for (MultipartFile file : attachments) {
                if (file.isEmpty()) continue;

                // 验证文件类型
                String contentType = file.getContentType();
                if (!FileStorageUtils.isValidFileType(contentType)) {
                    return Result.error("不支持的文件类型: " + contentType);
                }
                files.add(file);
            }

            return assignmentService.createAssignment(courseID,assignmentName,assignmentRequirements,deadline,beginTime,studentIdList,files);
        } catch (Exception e) {
            log.error("作业创建失败", e);
            return Result.error("作业创建失败: " + e.getMessage());
        }
    }

    @GetMapping("/GetAssignmentSubmissionsPage")
    public ToPage<AssignmentSubmissions> GetAssignmentSubmissionsPage(Integer AssignmentId, String gradeStatus, PageQuery query){
        //根据作业id获取所有提交数据
        GradeStatus Status = GradeStatus.getGradeStatusByDesc(gradeStatus);
        return assignmentSubmissionsService.GetPage(AssignmentId,Status,query);
    }

    @PostMapping("/AssignmentSubmissionsGarde")
    public Result GardeAssignmentSubmissions(
            @RequestParam("submissionId") Integer submissionId,
            @RequestParam("score") Integer score,
            @RequestParam("bonusPoints") Integer bonusPoints,
            @RequestParam(value = "feedback", required = false) String feedback
    ) {
        // 返回批改结果
        return assignmentService.SubmissionsGarde(submissionId,score,bonusPoints,feedback);
    }
}
