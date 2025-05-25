package com.did.docdiffserver.data.vo;

import lombok.Data;

@Data
public class ProcessFileResponseVO extends BaseResponseVo {

    private String fileId;

    private String text;

}
