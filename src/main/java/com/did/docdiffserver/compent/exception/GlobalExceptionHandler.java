package com.did.docdiffserver.compent.exception;

import com.did.docdiffserver.data.base.ResponseContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 处理所有未被捕获的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler  {


    // 处理自定义业务异常
    @ResponseBody
    @ExceptionHandler({BusinessException.class})
    public ResponseContent bizException(BusinessException e) {
        log.error(e.getMessage(), e);
        return ResponseContent.failedOf(e);
    }


    // 处理所有未捕获的异常
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseContent handleAllExceptions(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseContent.failedOf(ErrorCode.SYSTEM_ERROR);

    }


}
