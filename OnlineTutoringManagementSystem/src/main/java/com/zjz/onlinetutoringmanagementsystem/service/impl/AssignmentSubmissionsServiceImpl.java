package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.enums.GradeStatus;
import com.zjz.onlinetutoringmanagementsystem.mapper.*;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IFileInfoService;
import com.zjz.onlinetutoringmanagementsystem.service.IParentsService;
import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.zjz.pojo.*;
import com.zjz.onlinetutoringmanagementsystem.service.IAssignmentSubmissionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-02-18
 */
@Service
public class AssignmentSubmissionsServiceImpl extends ServiceImpl<AssignmentSubmissionsMapper, AssignmentSubmissions> implements IAssignmentSubmissionsService {

    @Autowired
    AssignmentMapper assignmentMapper;

    @Autowired
    AssignmentSubmissionsMapper submissionsMapper;

    @Autowired
    IUsersService usersService;

    @Autowired
    IFileInfoService fileInfoService;

    @Autowired
    IParentsService parentsService;

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    StudentsMapper studentsMapper;

    @Override
    public Result AssignmentSubmit(Integer assignmentId, String content, List<MultipartFile> files) throws Exception {
        try {
            Integer studentId = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());
            QueryWrapper<AssignmentSubmissions> queryWrapper = new QueryWrapper<>();
            queryWrapper
                    .eq("student_id",studentId)
                    .eq("assignment_id",assignmentId);

            if (submissionsMapper.exists(queryWrapper)) {
                throw new Exception("请勿重复提交");
            }
            AssignmentSubmissions submissions = new AssignmentSubmissions();
            submissions.setStudentId(studentId);
            submissions.setContent(content);
            submissions.setSubmitTime(LocalDateTime.now());
            submissions.setAssignmentId(assignmentId);
            submissionsMapper.insert(submissions);

            if (files != null && !files.isEmpty()){
                List<Integer> filesIdList = fileInfoService.FilesSave(files, "Assignmentsubmissions", studentId, submissions.getSubmissionId());
                String filesIdListJSON = JSON.toJSONString(filesIdList);

                submissions.setAttachmentIds(filesIdListJSON);
            }
            submissionsMapper.updateById(submissions);
        }catch (Exception e){
            throw e;
        }
        return Result.success();
    }

    @Override
    public ToPage<AssignmentSubmissions> GetPage(Integer AssignmentId, GradeStatus gradeStatus, PageQuery query) {

//        String role = SecurityUtils.getCurrentUserRole();
//        String username = SecurityUtils.getCurrentUsername();
//        Integer id = usersService.selectIdByUserName(username);

        Page<AssignmentSubmissions> page = query.toMpPage();

        QueryWrapper<AssignmentSubmissions> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("grade_status",gradeStatus)
                .eq("assignment_id",AssignmentId);

        //查询，传入分页和查询条件
        page(page, queryWrapper);
        //返回数据
        return ToPage.of(page, AssignmentSubmissions.class);
    }

    @Override
    public Result CheckSubmit(Integer assignmentId) {

        String role = SecurityUtils.getCurrentUserRole();
        Integer studentId = 0;

        assert role != null;
        if (role.equals("ROLE_student")){
            studentId = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());
        }else if(role.equals("ROLE_parent")){
            studentId = parentsService.getStudentIdByUsername(SecurityUtils.getCurrentUsername());
        }

        QueryWrapper<AssignmentSubmissions> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assignment_id",assignmentId)
                .eq("student_id",studentId);

        if (submissionsMapper.exists(queryWrapper)){
            return Result.success(submissionsMapper.selectOne(queryWrapper).getGradeStatus().getDesc());
        }else {
            return Result.success("未提交");
        }
    }

    @Override
    public AssignmentSubmissions GetSubmission(Integer assignmentId) {

        String role = SecurityUtils.getCurrentUserRole();
        Integer studentId = 0;

        assert role != null;
        if (role.equals("ROLE_student")){
            studentId = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());
        }else if(role.equals("ROLE_parent")){
            studentId = parentsService.getStudentIdByUsername(SecurityUtils.getCurrentUsername());
        }
        QueryWrapper<AssignmentSubmissions> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assignment_id",assignmentId)
                .eq("student_id",studentId);

        return submissionsMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignmentSubmission(Integer assignmentId, String content, String deletedAttachmentIdsJson, MultipartFile[] attachments) {
        Integer studentId = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());

        // 1. 查询当前用户对应的作业提交记录
        QueryWrapper<AssignmentSubmissions> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assignment_id", assignmentId)
                .eq("student_id", studentId);
        AssignmentSubmissions submissions = submissionsMapper.selectOne(queryWrapper);

        // 2. 解析 deletedAttachmentIdsJson 为 List<Integer>
        List<Integer> deletedAttachmentIds = new ArrayList<>();
        if (deletedAttachmentIdsJson != null && !deletedAttachmentIdsJson.isEmpty()) {
            deletedAttachmentIds = JSON.parseArray(deletedAttachmentIdsJson, Integer.class);
        }

        // 3. 处理新增附件
        List<Integer> newAttachmentIds = new ArrayList<>();
        if (attachments != null && attachments.length > 0) {
            // 转换 attachments 到 List<MultipartFile>
            List<MultipartFile> fileList = Arrays.asList(attachments);
            newAttachmentIds = fileInfoService.FilesSave(fileList, "Assignmentsubmissions", studentId, submissions.getSubmissionId());
        }

        // 4. 更新 submission 的 content 和 attachmentIds
        submissions.setContent(content);

        // 5. 更新 attachmentIds 字段
        List<Integer> attachmentIds = new ArrayList<>();
        if (submissions.getAttachmentIds() != null) {
            // 将 attachmentIds 从 JSON 字符串转换为 List<Integer>
            attachmentIds = JSON.parseArray(submissions.getAttachmentIds(), Integer.class);
        }

        // 移除已删除的文件 ID
        attachmentIds.removeAll(deletedAttachmentIds);

        // 添加新增的文件 ID
        attachmentIds.addAll(newAttachmentIds);

        // 重新设置 attachmentIds
        if (attachmentIds.isEmpty()) {
            submissions.setAttachmentIds(null);
        } else {
            submissions.setAttachmentIds(JSON.toJSONString(attachmentIds));
        }

        // 6. 更新 submission 的 content 和 attachmentIds
        submissionsMapper.updateById(submissions);

        // 7. 批量删除文件（现在才执行删除操作，确保数据库中的 attachmentIds 已经更新）
        if (!deletedAttachmentIds.isEmpty()) {
            fileInfoService.deleteFilesByIdList(deletedAttachmentIds);
        }
    }
}
