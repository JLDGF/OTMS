package com.zjz.onlinetutoringmanagementsystem.controller;


import com.zjz.enums.ActivityStatus;
import com.zjz.onlinetutoringmanagementsystem.service.IActivityService;
import com.zjz.onlinetutoringmanagementsystem.service.IStudentsService;
import com.zjz.onlinetutoringmanagementsystem.service.ITeachersService;
import com.zjz.pojo.Activity;
import com.zjz.pojo.Result;
import com.zjz.pojo.Students;
import com.zjz.pojo.Teachers;
import com.zjz.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zjz
 * @since 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    IActivityService activityService;

    @Autowired
    ITeachersService teachersService;

    @Autowired
    IStudentsService studentsService;

    //家长和教师所用的预约功能
    @PostMapping("/addActivity")
    public Result AddActivity(@RequestBody Activity activity) throws Exception {

        log.info(activity.toString());

        return activityService.AddActivity(activity);
    }

    //用户同意或拒绝预约
    @PutMapping("/auditActivity")
    public Result UserActivityAudit(@RequestBody Map<String, Object> request){

        try {
            //获取ID和审核信息
            Integer activityId = (Integer) request.get("activityId");
            Boolean auditResult = Boolean.parseBoolean((String) request.get("auditResult"));
            log.info(auditResult.toString());
            //根据审核信息，改变审核状态，接受时更新学生-课程对应表
            if (auditResult){
                return activityService.agree(activityId);
            }else {
                return activityService.reject(activityId);
            }
        }catch (Exception e){
            log.info("error:{}",e.toString());
            return Result.error(e.toString());
        }
    }

    //给已完成的活动的教师评分
    @PutMapping("rateActivity")
    public Result rateActivity(@RequestParam Integer ActivityId,@RequestParam Integer Point){

        if (activityService.getById(ActivityId)!=null && Point > 0 && Point <= 5){

            Activity activity = activityService.getById(ActivityId);
            String role = SecurityUtils.getCurrentUserRole();
            assert role != null;
            //根据角色和状态更新总积分、总体评分和活动状态
            if (role.equals("ROLE_student")&&activity.getActivityStatus().equals(ActivityStatus.COMPLETED)){
                activity.setActivityStatus(ActivityStatus.SEVALUATED);
                Teachers teacher =  teachersService.getById(activity.getTeacherId());
                teacher.setTotalPoints(teacher.getTotalPoints()+Point);
                teacher.setOverallRating((float) (teacher.getTotalPoints()/teacher.getActivityCount()));
                teachersService.updateById(teacher);
            }else if (role.equals("ROLE_teacher")&&activity.getActivityStatus().equals(ActivityStatus.COMPLETED)){
                activity.setActivityStatus(ActivityStatus.TEVALUATED);
                Students students = studentsService.getById(activity.getStudentId());
                students.setTotalPoints(students.getTotalPoints()+Point);
                students.setOverallRating((float) (students.getTotalPoints()/students.getActivityCount()));
                studentsService.updateById(students);
            }else if (role.equals("ROLE_student")&&activity.getActivityStatus().equals(ActivityStatus.TEVALUATED)){
                activity.setActivityStatus(ActivityStatus.OVER);
                Teachers teacher =  teachersService.getById(activity.getTeacherId());
                teacher.setTotalPoints(teacher.getTotalPoints()+Point);
                teacher.setOverallRating((float) (teacher.getTotalPoints()/teacher.getActivityCount()));
                teachersService.updateById(teacher);
            }else if (role.equals("ROLE_teacher")&&activity.getActivityStatus().equals(ActivityStatus.SEVALUATED)){
                activity.setActivityStatus(ActivityStatus.OVER);
                Students students = studentsService.getById(activity.getStudentId());
                students.setTotalPoints(students.getTotalPoints()+Point);
                students.setOverallRating((float) (students.getTotalPoints()/students.getActivityCount()));
                studentsService.updateById(students);
            }else {
                return Result.error("未知错误，请联系管理员");
            }

            activityService.updateById(activity);
            return Result.success();
        }else {
            return Result.error("非法参数");
        }
    }

}
