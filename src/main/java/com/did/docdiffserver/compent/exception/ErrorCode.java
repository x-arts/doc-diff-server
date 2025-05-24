package com.did.docdiffserver.compent.exception;

public enum ErrorCode {





    ;
    public final String code;
    public final String message;


    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
