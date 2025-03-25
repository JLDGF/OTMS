package com.zjz.onlinetutoringmanagementsystem.controller.parent;


import com.zjz.enums.ActivityStatus;
import com.zjz.onlinetutoringmanagementsystem.service.IActivityService;
import com.zjz.onlinetutoringmanagementsystem.service.IEnrollmentService;
import com.zjz.onlinetutoringmanagementsystem.service.IFeeTransactionsService;
import com.zjz.onlinetutoringmanagementsystem.service.ITeachersService;
import com.zjz.pojo.Activity;
import com.zjz.pojo.Enrollment;
import com.zjz.pojo.Result;
import com.zjz.pojo.Teachers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
@Slf4j
@RestController
@RequestMapping("/parents")
public class ParentsController {

    @Autowired
    private IEnrollmentService enrollmentService;

    @Autowired
    private IFeeTransactionsService feeTransactionsService;


    //报名课程
    @GetMapping("/enroll")
    public Result enroll(Integer courseId ,Integer teacherId) throws Exception {

        log.info("courseId : {}",courseId);

        if (enrollmentService.ParentEnroll(courseId,teacherId)){
            return Result.success();
        }else {
            return Result.error("报名失败");
        }
    }

    //支付账单
    @PutMapping("payFee")
    public Result PayFeeById(Integer transactionId){
        try {
            return feeTransactionsService.PayById(transactionId);
        }catch (Exception e){
            log.error(e.toString());
            return Result.error("支付失败");
        }
    }

}
