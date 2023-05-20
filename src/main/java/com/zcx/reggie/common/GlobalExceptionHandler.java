package com.zcx.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})   // 拦截指定的Controller，以处理异常
@ResponseBody   // 将结果封装为JSON数据
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @return 返回错误信息
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        if (e.getMessage().contains("Duplicate entry")) {
            String msg = "[" + e.getMessage().split(" ")[2].replace("'", "") + "]已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
}
