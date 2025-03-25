package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.enums.ApprovalsStatus;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Approvals;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Result;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-03-03
 */
public interface IApprovalsService extends IService<Approvals> {

    Result getApprovalsByQueryAndStatus(ApprovalsStatus status, PageQuery query);

    Result userApprovals(String content);

    Result adminGetAuditTime(LocalDateTime startTime, LocalDateTime endTime);
}
