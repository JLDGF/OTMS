package com.zjz.onlinetutoringmanagementsystem.exception;


import com.zjz.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result ex(Exception ex){
        ex.printStackTrace();
        log.error("Exception occurred", ex);

        // 先检查 null，再检查是否为空字符串
        if (ex.getMessage() == null || ex.getMessage().isEmpty()){
            return Result.error("操作失败，请联系管理员");
        }
        return Result.error("操作失败 ：" + ex.getMessage());
    }
}
