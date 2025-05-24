package com.did.docdiffserver.data.base;


import com.did.docdiffserver.compent.exception.BusinessException;
import com.did.docdiffserver.compent.exception.ErrorCode;
import lombok.Data;

/**
 * responseBody
 */
@Data
public class ResponseContent {

    private String code;

    private String message;

    private BaseVO data;



    public static ResponseContent failedOf(BusinessException exception) {
        ResponseContent content = new ResponseContent();
        content.setCode(exception.getCode());
        content.setMessage(exception.getMessage());
        return content;
    }

    public static ResponseContent failedOf(ErrorCode errorCode) {
        ResponseContent content = new ResponseContent();
        content.setCode(errorCode.getCode());
        content.setMessage(errorCode.getMessage());
        return content;
    }


}
