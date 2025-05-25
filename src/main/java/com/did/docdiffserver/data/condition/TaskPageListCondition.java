package com.did.docdiffserver.data.condition;

import com.did.docdiffserver.data.base.BasePageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TaskPageListCondition extends BasePageCondition {

    private String taskName;

    private String sorter;

    private String filter;
}
