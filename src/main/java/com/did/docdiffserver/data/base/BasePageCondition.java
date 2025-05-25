package com.did.docdiffserver.data.base;

import lombok.Data;

@Data
public class BasePageCondition implements BaseCondition {

    private Integer current;

    private Integer pageSize;
}
