package com.zjz.onlinetutoringmanagementsystem.controller.student;


import com.zjz.enums.ActivityStatus;
import com.zjz.onlinetutoringmanagementsystem.service.IActivityService;
import com.zjz.onlinetutoringmanagementsystem.service.IAssignmentSubmissionsService;
import com.zjz.onlinetutoringmanagementsystem.service.ITeachersService;
import com.zjz.pojo.Activity;
import com.zjz.pojo.AssignmentSubmissions;
import com.zjz.pojo.Result;
import com.zjz.pojo.Teachers;
import com.zjz.utils.FileStorageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
@RestController
@Slf4j
@RequestMapping("/students" )
public class StudentsController {

    @Autowired
    IAssignmentSubmissionsService assignmentSubmissionsService;

    @Autowired
    private IActivityService activityService;
    @Autowired
    private ITeachersService teachersService;

    //提交作业
    @PostMapping("CreateAssignmentSubmission" )
    public Result StudentAssignmentSubmit(
            @RequestParam Integer assignmentId,
            @RequestParam String content,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        try {
            if (attachments==null){
                return assignmentSubmissionsService.AssignmentSubmit(assignmentId, content, null);
            }

            // 处理文件上传
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

            return assignmentSubmissionsService.AssignmentSubmit(assignmentId, content, files);
        } catch (Exception e) {
            log.error("作业创建失败", e);
            return Result.error("作业创建失败: " + e.getMessage());
        }
    }
    //更新作业
    @PostMapping("UpdateAssignmentSubmission")
    public Result updateAssignmentSubmission(
            @RequestParam Integer assignmentId,
            @RequestParam String content,
            @RequestParam(value = "deletedAttachmentIds", required = false) String deletedAttachmentIdsJson,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments)
    {


        assignmentSubmissionsService.updateAssignmentSubmission(assignmentId,content,deletedAttachmentIdsJson,attachments);
        return Result.success("编辑成功");
    }

}

