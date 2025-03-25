package com.zjz.onlinetutoringmanagementsystem.controller.admin;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IApprovalsService;
import com.zjz.enums.ApprovalsStatus;
import com.zjz.pojo.Approvals;
import com.zjz.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/admin/approvals")
public class adminApprovalsController {

    @Autowired
    private IApprovalsService approvalsService;

    // 删
    @DeleteMapping("delete")
    public Result adminDeleteApprovals(Integer auditId) {
        try {
            approvalsService.removeById(auditId);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("删除失败");
        }
    }

    // 改
    @PutMapping("update")
    public Result adminUpdateApprovals(@RequestBody Approvals approvals) {
        try {
            approvals.setUpdatedAt(LocalDateTime.now());
            approvalsService.updateById(approvals);
            return Result.success("更新成功");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("更新失败");
        }
    }

    // 查
    @GetMapping("list")
    public Result adminGetApprovalsByQuery(PageQuery query, @RequestParam(required = false) ApprovalsStatus status) {
        try {
            return approvalsService.getApprovalsByQueryAndStatus(status, query);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }
}