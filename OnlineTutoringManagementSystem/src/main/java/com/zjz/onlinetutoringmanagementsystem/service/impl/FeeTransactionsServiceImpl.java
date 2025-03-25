package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IParentsService;
import com.zjz.onlinetutoringmanagementsystem.service.ITeachersService;
import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.zjz.pojo.*;
import com.zjz.onlinetutoringmanagementsystem.mapper.FeeTransactionsMapper;
import com.zjz.onlinetutoringmanagementsystem.service.IFeeTransactionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
 * @since 2025-03-03
 */
@Service
public class FeeTransactionsServiceImpl extends ServiceImpl<FeeTransactionsMapper, FeeTransactions> implements IFeeTransactionsService {

    @Autowired
    private IUsersService usersService;

    @Autowired
    private IParentsService parentsService;

    @Autowired
    private ITeachersService teachersService;

    @Autowired
    private FeeTransactionsMapper feeTransactionsMapper;

    //用户获取账单列表
    @Override
    public Result userGetFeeList(PageQuery query, String status) {
        Page<FeeTransactions> page = query.toMpPage();
        QueryWrapper<FeeTransactions> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("fee_status", status.equals("true"));
        }

        String role = SecurityUtils.getCurrentUserRole();
        // 根据角色设置查询条件
        assert role != null;
        if (role.equals("ROLE_parent")) {
            // 只能看到与自己相关的记录
            queryWrapper.eq("student_id", parentsService.getStudentIdByUsername(SecurityUtils.getCurrentUsername()));
            // 家长只能看到部分字段
            queryWrapper.select("transaction_id", "activity_id", "teacher_id", "real_fee_amount", "fee_status", "payment_date", "created_at", "updated_at");
        } else if (role.equals("ROLE_teacher")) {
            // 只能看到与自己相关的记录
            queryWrapper.eq("teacher_id", usersService.selectIdByUserName(SecurityUtils.getCurrentUsername()));
            // 教师只能看到部分字段
            queryWrapper.select("transaction_id", "activity_id", "student_id", "fee_amount", "fee_status", "payment_date", "created_at", "updated_at");
        }

        page(page, queryWrapper);
        return Result.success(ToPage.of(page, FeeTransactions.class));
    }

    @Override
    public Result PayById(Integer transactionId) {
        //1.先查找相关方
        FeeTransactions fee = feeTransactionsMapper.selectById(transactionId);
        Integer parentId = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());

        QueryWrapper<Parents> queryWrapperP = new QueryWrapper<>();
        queryWrapperP.eq("student_user_id", fee.getStudentId());

        Parents parent = new Parents();
        // 2.检验
        //确认当前操作家长为账单所有者
        if (parentId.equals(parentsService.getOne(queryWrapperP).getParentUserId())) {
            parent = parentsService.getById(parentId);
        } else {
            return Result.error("操作非法");
        }

        Teachers teacher = teachersService.getById(fee.getTeacherId());

        // 3.更新数据
        parent.setConfirmPrice(parent.getConfirmPrice().subtract(fee.getRealFeeAmount()));
        teacher.setSettlementPrice(teacher.getSettlementPrice().add(fee.getFeeAmount()));

        fee.setFeeStatus(true);
        fee.setPaymentDate(LocalDateTime.now());
        fee.setUpdatedAt(LocalDateTime.now());

        //4.存入数据
        parentsService.updateById(parent);
        teachersService.updateById(teacher);
        feeTransactionsMapper.updateById(fee);

        return Result.success("支付成功");

    }

    @Override
    public Result adminGetBillTrend(LocalDateTime startTime, LocalDateTime endTime) {

        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();

        List<Map<String, Object>> mapList = new ArrayList<>();

        for (; !start.isAfter(end); start = start.plusDays(1)) {

            QueryWrapper<FeeTransactions> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("created_at", start, start.plusDays(1));
            List<FeeTransactions> listAll = this.list(queryWrapper);
            queryWrapper.eq("fee_status", true);
            List<FeeTransactions> listAlready = this.list(queryWrapper);

            Map<String, Object> FeeMap = new HashMap<>();

            FeeMap.put("date", start.format(DateTimeFormatter.ISO_DATE));
            FeeMap.put("paid", listAll.stream().map(FeeTransactions::getFeeAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            FeeMap.put("due", listAlready.stream().map(FeeTransactions::getFeeAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            mapList.add(FeeMap);
        }

        return Result.success(mapList);
    }
}
