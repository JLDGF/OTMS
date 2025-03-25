package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.enums.GradeStatus;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.AssignmentSubmissions;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-02-18
 */
public interface IAssignmentSubmissionsService extends IService<AssignmentSubmissions> {

    Result AssignmentSubmit(Integer assignmentId, String content, List<MultipartFile> files) throws Exception;

    ToPage<AssignmentSubmissions> GetPage(Integer assignmentId, GradeStatus gradeStatus, PageQuery query);

    Result CheckSubmit(Integer assignmentId);

    AssignmentSubmissions GetSubmission(Integer assignmentId);

    void updateAssignmentSubmission(Integer assignmentId, String content, String deletedAttachmentIdsJson, MultipartFile[] attachments);
}
