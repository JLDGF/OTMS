package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.enums.ActivityStatus;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Activity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-01-15
 */
public interface IActivityService extends IService<Activity> {
    //已完成
    ToPage<Activity> queryCompletedActivityPageByUsername(PageQuery query) throws Exception;
    //未完成
    ToPage<Activity> queryUpcomingActivityPageByUsername(PageQuery query) throws Exception;
    //已失效
    ToPage<Activity> queryInvalidActivityPageByUsername(PageQuery query) throws Exception;
    //根据课程
    ToPage<Activity> queryActivityPageByCourse(PageQuery query, Integer courseId) throws Exception;
    //查询详细信息
    Result getActivityDetails(Integer activityId) throws Exception;

    Result AddActivity(Activity activity) throws Exception;

    Result agree(Integer activityId);

    Result reject(Integer activityId);

    ToPage<Activity> adminGetActivityPage(PageQuery query, String activityName, ActivityStatus status, Integer courseId, Integer teacherId, Integer studentId, LocalDateTime startTime, LocalDateTime endTime);

    Result adminGetActivityTrend(LocalDateTime startTime, LocalDateTime endTime);
}
