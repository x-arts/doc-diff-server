package com.did.docdiffserver.compent.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SYSTEM_ERROR("500", "服务器内部错误"),
    REQUEST_PARAM_ERROR("400", "请求参数错误"),
    REQUEST_FILE_SIZE_UP_LIMIT("413", "文件大小超过限制"),
    REQUEST_FILE_TYPE_NOT_SUPPORT("415", "不支持的文件类型"),

    ;
    public final String code;
    public final String message;


    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
