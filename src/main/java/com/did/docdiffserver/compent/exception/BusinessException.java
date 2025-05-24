package com.did.docdiffserver.compent.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessException extends RuntimeException {

    private Integer code;

    private String message;


    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode.code, errorCode.message);
    }

    public static BusinessException of(int code, String message) {
        return new BusinessException(code, message);
    }


}
