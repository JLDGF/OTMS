package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.FeeTransactions;
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
public interface IFeeTransactionsService extends IService<FeeTransactions> {

    Result userGetFeeList(PageQuery query, String status);

    Result PayById(Integer transactionId);

    Result adminGetBillTrend(LocalDateTime startTime, LocalDateTime endTime);
}
