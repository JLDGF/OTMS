package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.enums.ApprovalsStatus;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.zjz.pojo.Approvals;
import com.zjz.onlinetutoringmanagementsystem.mapper.ApprovalsMapper;
import com.zjz.onlinetutoringmanagementsystem.service.IApprovalsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import com.zjz.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
public class ApprovalsServiceImpl extends ServiceImpl<ApprovalsMapper, Approvals> implements IApprovalsService {

    @Autowired
    IUsersService usersService;

    @Autowired
    ApprovalsMapper approvalsMapper;

    @Override
    public Result getApprovalsByQueryAndStatus(ApprovalsStatus status, PageQuery query) {
        Page<Approvals> page = query.toMpPage();

        QueryWrapper<Approvals> queryWrapper = new QueryWrapper<>();
        if (status !=  null){
            queryWrapper.eq("approvals_status",status);
        }

        page(page,queryWrapper);
        return Result.success(ToPage.of(page,Approvals.class));
    }

    @Override
    public Result userApprovals(String content) {
        //获取用户ID
        Integer id = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());

        QueryWrapper<Approvals> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id",id)
                .eq("approvals_status", ApprovalsStatus.PENDING);

        if (approvalsMapper.exists(queryWrapper)){
            return Result.error("请求审核中，请耐心等待。");
        }
        //构造申请
        Approvals approvals = new Approvals();
        approvals.setContent(content);
        approvals.setUserId(id);

        approvalsMapper.insert(approvals);
        return Result.success();
    }

    @Override
    public Result adminGetAuditTime(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 生成完整日期范围
            List<LocalDate> dateList = new ArrayList<>();
            LocalDate start = startTime.toLocalDate();
            LocalDate end = endTime.toLocalDate();

            while (!start.isAfter(end)) {
                dateList.add(start);
                start = start.plusDays(1);
            }

            // 2. 初始化默认值为0的映射表
            Map<LocalDate, Double> dateAvgMap = dateList.stream()
                    .collect(Collectors.toMap(
                            localDate -> localDate,
                            localDate -> 0.0
                    ));

            // 3. 构建查询条件（修复时间范围查询）
            QueryWrapper<Approvals> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("created_at",
                    startTime.toLocalDate().atStartOfDay(),  // 转换为当天00:00:00
                    endTime.toLocalDate().atTime(23, 59, 59)) // 转换为当天23:59:59
                    .in("approvals_status", ApprovalsStatus.AGREE, ApprovalsStatus.REJECT);

            List<Approvals> approvalsList = this.list(queryWrapper);

            // 4. 分组统计并计算平均时间（修复统计逻辑）
            Map<LocalDate, DoubleSummaryStatistics> statsMap = approvalsList.stream()
                    .filter(a -> a.getUpdatedAt() != null) // 确保有更新时间
                    .collect(Collectors.groupingBy(
                            a -> a.getCreatedAt().toLocalDate(),
                            Collectors.summarizingDouble(a -> {
                                // 计算审核耗时（小时），保留1位小数
                                Duration duration = Duration.between(
                                        a.getCreatedAt(),
                                        a.getUpdatedAt()
                                );
                                return Math.round(duration.toMinutes() / 6.0 ) / 10.0; // 分钟转小时并保留1位小数
                            })
                    ));

            // 5. 更新平均值到映射表
            statsMap.forEach((date, stats) -> {
                if (stats.getCount() > 0) {
                    dateAvgMap.put(date, stats.getAverage());
                }
            });

            // 6. 构建返回数据结构（修复数据映射）
            List<Map<String, Object>> dataList = dateList.stream()
                    .map(date -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("date", date.format(DateTimeFormatter.ISO_DATE));
                        map.put("avgTime",
                                // 保留1位小数处理
                                BigDecimal.valueOf(dateAvgMap.get(date))
                                        .setScale(1, RoundingMode.HALF_UP)
                                        .doubleValue()
                        );
                        return map;
                    })
                    .collect(Collectors.toList());

            return Result.success(dataList);
        } catch (Exception e) {
            log.error("获取审核时间数据失败", e);
            return Result.error("数据查询异常");
        }
    }
}
