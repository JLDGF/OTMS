package com.zjz.onlinetutoringmanagementsystem.controller.admin;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IActivityService;
import com.zjz.pojo.Activity;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import com.zjz.enums.ActivityStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/admin/activity")
public class adminActivityController {

    @Autowired
    private IActivityService activityService;

    // 分页获取活动列表（筛选参数不同）
    @GetMapping("/AllActivityPage")
    public ToPage<Activity> adminGetActivityPage(
            PageQuery query,
            @RequestParam(required = false) String activityName,
            @RequestParam(required = false) ActivityStatus status,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer teacherId,
            @RequestParam(required = false) Integer studentId,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime
    ) {
        log.info("活动分页查询：页码{}，页大小{}", query.getPageNo(), query.getPageSize());
        return activityService.adminGetActivityPage(
                query, activityName, status, courseId, teacherId, studentId, startTime, endTime
        );
    }

    // 新增活动
    @PostMapping("/add")
    public Result createActivity(@RequestBody Activity activity) throws Exception {
        log.info("新增活动：{}", activity.getActivityName());
        try{
            return activityService.AddActivity(activity);
        }catch (Exception e){
            log.error(e.toString());
            return Result.error("新增失败,请检查数据完整性");
        }

    }

    // 更新活动
    @PutMapping("/update")
    public Result updateActivity(@RequestBody Activity activity) throws Exception {
        log.info("更新活动：ID{}", activity.getActivityId());
        activityService.updateById(activity);
        return Result.success("活动更新成功");
    }

    // 删除活动
    @DeleteMapping("/delete")
    public Result deleteActivity(@RequestParam Integer activityId) throws Exception {
        log.info("删除活动：ID{}", activityId);
        if(activityService.removeById(activityId)){
            return Result.success();
        }else {
            return Result.error("删除失败");
        }
    }
}