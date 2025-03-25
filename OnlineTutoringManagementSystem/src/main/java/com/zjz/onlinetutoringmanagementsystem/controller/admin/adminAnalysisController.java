package com.zjz.onlinetutoringmanagementsystem.controller.admin;

import com.zjz.onlinetutoringmanagementsystem.service.*;
import com.zjz.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/admin/analysis")
public class adminAnalysisController {

    @Autowired
    private IUsersService usersService;

    @Autowired
    private IPostsService postsService;

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IFeeTransactionsService feeTransactionsService;

    @Autowired
    private IApprovalsService approvalsService;

    //新用户角色分布
    @GetMapping("GetUserRole")
    public Result adminGetUserRole(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ) {
        try {
            return usersService.adminGetUserRole(startTime, endTime);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }
    //新用户趋势
    @GetMapping("GetNewUsers")
    public Result adminGetNewUser(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ) {
        try {
            return usersService.adminGetNewUser(startTime, endTime);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }
    //发帖趋势
    @GetMapping("GetPostTrend")
    public Result adminGetPostTrend(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ){
        try {
            return postsService.adminGetPostTrend(startTime,endTime);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }
    //预约趋势
    @GetMapping("GetActivityTrend")
    public Result adminGetActivityTrend(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ){
        try {
            return activityService.adminGetActivityTrend(startTime,endTime);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }
    //账单趋势
    @GetMapping("GetBillTrend")
    public Result adminGetBillTrend(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ){
        try {
            return feeTransactionsService.adminGetBillTrend(startTime,endTime);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }

    //平均审核时间
    @GetMapping("GetAuditTime")
    public Result adminGetAuditTime(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ){
        try {
            return approvalsService.adminGetAuditTime(startTime,endTime);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }



}
