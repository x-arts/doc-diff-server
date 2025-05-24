package com.did.docdiffserver.compent.exception;

import com.did.docdiffserver.data.base.ResponseContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 全局异常处理器
 * 处理所有未被捕获的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler  {



    // 处理自定义业务异常
//    @ResponseBody
//    @ExceptionHandler({BusinessException.class})
//    public ResponseContent bizException(BusinessException e) {
////        log.error("出现异常:{}", e);
//        return ResultsBean.failed(e.getMessage());
//    }


    // 处理所有未捕获的异常
//    @ResponseBody
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "服务器内部错误: " + ex.getMessage(),
//                System.currentTimeMillis()
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }


}
