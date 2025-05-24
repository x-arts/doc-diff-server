package com.did.docdiffserver.data.vo;

import lombok.Data;

@Data
public class BaseResponseVo {

    private String code;

    private String message;


    public static BaseResponseVo success() {
        BaseResponseVo baseResponseVo = new BaseResponseVo();
        baseResponseVo.setCode("0000");
        baseResponseVo.setMessage("success");
        return baseResponseVo;
    }

    public static BaseResponseVo nSuccess(String message) {
        BaseResponseVo baseResponseVo = new BaseResponseVo();
        baseResponseVo.setCode("0001");
        baseResponseVo.setMessage(message);
        return baseResponseVo;
    }

    public void successStatus() {
        this.code = "0000";
        this.message = "success";
    }

}
