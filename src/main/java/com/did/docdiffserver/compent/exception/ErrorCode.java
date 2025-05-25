package com.did.docdiffserver.compent.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {


    SUCCESS(200, "操作成功"),

    SYSTEM_ERROR(500, "服务器内部错误"),


    REQUEST_PARAM_ERROR(400, "请求参数错误"),
    REQUEST_FILE_SIZE_UP_LIMIT(413, "文件大小超过限制"),
    REQUEST_FILE_TYPE_NOT_SUPPORT(415, "不支持的文件类型"),

    FILE_NOT_EMPTY(414, "文件内容不能为空"),
    ILLEGAL_NOT_ASSERT(416, "参数非预期值"),

    ;
    public final int code;
    public final String message;


    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
