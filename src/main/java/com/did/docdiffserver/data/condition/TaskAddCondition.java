package com.did.docdiffserver.data.condition;

import com.did.docdiffserver.data.base.BaseCondition;
import lombok.Data;

@Data
public class TaskAddCondition implements BaseCondition {

    private String taskName;

    private String standardFileId;

    private String compareFileId;


}
