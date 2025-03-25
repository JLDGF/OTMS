package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.enums.AssignmentStatus;
import com.zjz.enums.GradeStatus;
import com.zjz.onlinetutoringmanagementsystem.mapper.*;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.*;
import com.zjz.pojo.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-02-14
 */
@Service
public class AssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements IAssignmentService {

    @Autowired
    AssignmentMapper assignmentMapper;

    @Autowired
    AssignmentSubmissionsMapper submissionsMapper;

    @Autowired
    IUsersService usersService;

    @Autowired
    IStudentsService studentsService;

    @Autowired
    IParentsService parentsService;

    @Autowired
    IFileInfoService fileInfoService;

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    StudentsMapper studentsMapper;

    //新建作业
    @Override
    public Result createAssignment(Integer courseID, String assignmentName, String assignmentRequirements, LocalDateTime deadline, LocalDateTime beginTime, List<Long> studentIdList, List<MultipartFile> files) throws Exception {

        try {
            Integer teacherId = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());

            // 构造搜索条件
            QueryWrapper<Assignment> queryWrapper = new QueryWrapper<>();
            queryWrapper
                    .eq("assignment_name", assignmentName)
                    .eq("course_id", courseID)
                    .eq("teacher_id", teacherId);

            if (assignmentMapper.exists(queryWrapper)) {
                return Result.error("已存在的作业名");
            }

            // 构建作业对象并保存到数据库
            Assignment assignment = new Assignment();
            assignment.setAssignmentName(assignmentName);
            assignment.setCourseId(courseID);
            assignment.setTeacherId(teacherId);
            assignment.setStartTime(beginTime);
            assignment.setEndTime(deadline);

            int insertResult = assignmentMapper.insert(assignment);
            if (insertResult <= 0) {
                throw new RuntimeException("作业插入失败");
            }

            if (files != null && !files.isEmpty()) {
                List<Integer> filesIdList = fileInfoService.FilesSave(files, "Assignment", teacherId, assignment.getAssignmentId());
                String filesIdListJSON = JSON.toJSONString(filesIdList);
                assignment.setAttachmentId(filesIdListJSON);
            }

            // 将 List 转换为 JSON 字符串
            String studentIdListJSON = JSON.toJSONString(studentIdList);

            assignment.setAssignmentRequirements(assignmentRequirements);
            assignment.setReleaseTarget(studentIdListJSON);

            int updateResult = assignmentMapper.updateById(assignment);
            if (updateResult <= 0) {
                throw new RuntimeException("作业更新失败");
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            return Result.error("出现空指针异常，请检查输入参数是否为空");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return Result.success();
    }

    //获取自己参与的课程
    @Override
    public ToPage<Assignment> queryAssignmentPageByUserId(PageQuery query, AssignmentStatus assignmentStatus) {

        String role = SecurityUtils.getCurrentUserRole();
        String username = SecurityUtils.getCurrentUsername();
        Integer id = usersService.selectIdByUserName(username);

        //条件构造
        Page<Assignment> page = query.toMpPage();
        QueryWrapper<Assignment> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<Assignment> updateWrapper = new UpdateWrapper<>();

        assert role != null;
        if (role.equals("ROLE_teacher")) {
            queryWrapper
                    .eq("assignment_status", assignmentStatus)
                    .eq("teacher_id", id);
            //查询前更新状态
            updateWrapper
                    .eq("assignment_status", assignmentStatus)
                    .eq("teacher_id", id);
            updateAssignmentBeforeSearch(updateWrapper);
            //查询，传入分页和查询条件
            page(page, queryWrapper);
            //返回数据
            return ToPage.of(page, Assignment.class);
        } else if (role.equals("ROLE_student")) {
            queryWrapper
                    .eq("assignment_status", assignmentStatus)
                    .apply("JSON_CONTAINS(release_target, {0})", id.toString());

            //查询前更新状态
            updateWrapper.eq("assignment_status", assignmentStatus)
                    .apply("JSON_CONTAINS(release_target, {0})", id.toString());
            updateAssignmentBeforeSearch(updateWrapper);
            //查询，传入分页和查询条件
            page(page, queryWrapper);
            //返回数据
            return ToPage.of(page, Assignment.class);
        } else if (role.equals("ROLE_parent")){
            id = parentsService.getById(id).getStudentUserId();
            queryWrapper
                    .eq("assignment_status", assignmentStatus)
                    .apply("JSON_CONTAINS(release_target, {0})", id.toString());

            //查询前更新状态
            updateWrapper.eq("assignment_status", assignmentStatus)
                    .apply("JSON_CONTAINS(release_target, {0})", id.toString());
            updateAssignmentBeforeSearch(updateWrapper);
            //查询，传入分页和查询条件
            page(page, queryWrapper);
            //返回数据
            return ToPage.of(page, Assignment.class);
        }
        return null;
    }

    //获取作业详情
    @Override
    public Result getAssignmentDetailsById(Integer assignmentId) {
        // 查询前更新状态
        UpdateWrapper<Assignment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("assignment_id", assignmentId);
        updateAssignmentBeforeSearch(updateWrapper);

        // 构造一个Map来存储将要发送到前端接口的参数
        Map<String, Object> assignmentMap = new HashMap<>();

        // 根据ID获取作业信息
        Assignment assignment = assignmentMapper.selectById(assignmentId);

        // 获取参与学生ID列表
        List<Integer> studentIds = JSON.parseArray(assignment.getReleaseTarget(), Integer.class);
        LambdaQueryWrapper<Students> wrappers = new LambdaQueryWrapper<>();
        wrappers.in(Students::getStudentUserId, studentIds);
        // 查询学生数据
        List<Students> students = studentsMapper.selectList(wrappers);
        // 转换学生数据为Map<String, Integer>
        Map<String, Integer> studentMap = students.stream()
                .collect(Collectors.toMap(Students::getStudentUsername, Students::getStudentUserId));

        // 获取附件ID列表
        List<Integer> fileIdList = JSON.parseArray(assignment.getAttachmentId(), Integer.class);
        // 判断附件是否存在
        boolean attachmentExist = fileIdList != null && !fileIdList.isEmpty();
        if (attachmentExist){
        LambdaQueryWrapper<FileInfo> wrapperf = new LambdaQueryWrapper<>();
        wrapperf.in(FileInfo::getFileId, fileIdList);
        // 查询附件数据
        List<FileInfo> fileInfos = fileInfoMapper.selectList(wrapperf);

        // 转换附件数据为Map<String, Map<String, Object>>
        // 附件处理
        Map<String, Map<String, Object>> fileMap = fileInfos.stream()
                .collect(Collectors.toMap(
                        FileInfo::getFileName,
                        file -> {
                            Map<String, Object> fileData = new HashMap<>();
                            fileData.put("id", file.getFileId());
                            fileData.put("size", file.getFileSize());
                            return fileData;
                        }
                ));
            assignmentMap.put("attachmentNamesAndIds", fileMap);
        }


        // 填充参数
        assignmentMap.put("courseName", courseMapper.selectById(assignment.getCourseId()).getCourseName()); // 可选字段，可以为 null
        assignmentMap.put("studentNamesAndIds", studentMap); // 可选字段，可以为 null
        assignmentMap.put("assignmentName", assignment.getAssignmentName());
        assignmentMap.put("assignmentRequirements", assignment.getAssignmentRequirements());
        assignmentMap.put("startTime", assignment.getStartTime()); // 可选字段，可以为 null
        assignmentMap.put("endTime", assignment.getEndTime()); // 可选字段，可以为 null
        assignmentMap.put("assignmentStatus", assignment.getAssignmentStatus());

        return Result.success(assignmentMap);
    }

    //作业批改
    @Override
    public Result SubmissionsGarde(Integer submissionId, Integer score, Integer bonusPoints, String feedback) {
        try {
            AssignmentSubmissions submissions = submissionsMapper.selectById(submissionId);


            //更改批改状态
            if (submissions.getGradeStatus().equals(GradeStatus.PENDING)){
                submissions.setGradeStatus(GradeStatus.GRADED);
            }

            submissions.setScore(score);
            submissions.setBonusPoints(bonusPoints);
            submissions.setFeedback(feedback);
            submissions.setGradeTime(LocalDateTime.now());

            //更新角色表中对应用户的积分
            Students students = studentsService.getById(submissions.getStudentId());
            students.setActivityCount(students.getActivityCount()+1);
            students.setTotalPoints(students.getTotalPoints()+submissions.getBonusPoints());
            students.setOverallRating((float) (students.getTotalPoints()/students.getActivityCount()));

            studentsService.updateById(students);
            submissionsMapper.updateById(submissions);
        }catch (Exception e){
            Result.error(e.toString());
        }
        return Result.success("批改完成");
    }

    @Override
    public ToPage<Assignment> GetAssignmentPageByCourseId(PageQuery query, Integer courseId) {
        Page<Assignment> page = query.toMpPage();

        QueryWrapper<Assignment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId)
                .select("assignment_id","assignment_name");

        page(page,queryWrapper);
        return ToPage.of(page,Assignment.class);
    }

    //每次查询前更新所有活动的状态
    public void updateAssignmentBeforeSearch(UpdateWrapper<Assignment> updateWrapper) {

        // 时间已过且状态为进行中，设置为结束
        updateWrapper.lt("end_time", new Date())
                .and(wrapper -> wrapper.eq("assignment_status", AssignmentStatus.ACTIVE));
        updateWrapper.set("assignment_status", AssignmentStatus.ENDED);
        assignmentMapper.update(null, updateWrapper);

    }

}

