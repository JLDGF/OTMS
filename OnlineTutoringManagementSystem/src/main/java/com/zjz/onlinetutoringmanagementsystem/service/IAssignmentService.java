package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.enums.AssignmentStatus;
import com.zjz.enums.GradeStatus;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Assignment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-02-14
 */
public interface IAssignmentService extends IService<Assignment> {

    Result createAssignment(Integer courseID, String AssignmentName, String AssignmentRequirements, LocalDateTime deadline, LocalDateTime beginTime, List<Long> studentIdList, List<MultipartFile> files) throws Exception;

    ToPage<Assignment> queryAssignmentPageByUserId(PageQuery query, AssignmentStatus assignmentStatus);

    Result getAssignmentDetailsById(Integer assignmentId);

    Result SubmissionsGarde(Integer submissionId, Integer score, Integer bonusPoints, String feedback);


    ToPage<Assignment> GetAssignmentPageByCourseId(PageQuery query, Integer courseId);
}
