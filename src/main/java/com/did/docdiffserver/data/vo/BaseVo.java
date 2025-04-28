package com.did.docdiffserver.data.vo;

import lombok.Data;

@Data
public class BaseVo {

    private String code;

    private String message;


    public static BaseVo success() {
        BaseVo baseVo = new BaseVo();
        baseVo.setCode("0000");
        baseVo.setMessage("success");
        return baseVo;
    }

    public static BaseVo nSuccess(String message) {
        BaseVo baseVo = new BaseVo();
        baseVo.setCode("0001");
        baseVo.setMessage(message);
        return baseVo;
    }

    public void successStatus() {
        this.code = "0000";
        this.message = "success";
    }

}
