package com.did.docdiffserver.compent;


import com.alibaba.fastjson2.JSONObject;

public interface JsonStrAbility {
    default String toJsonStr() {
        return JSONObject.toJSONString(this);
    }

}
