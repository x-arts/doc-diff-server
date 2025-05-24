package com.did.docdiffserver.data.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.did.docdiffserver.data.condition.TaskAddCondition;
import com.did.docdiffserver.data.enums.TaskProcessStatus;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "contract_diff_task")
public class ContractDiffTask {


    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskName;

    /**
     * @see com.did.docdiffserver.data.enums.TaskProcessStatus
     */
    private String processStatus;

    private String standardFileId;

    private String compareFileId;

    private Date createTime;


    public static  ContractDiffTask createAddOf(TaskAddCondition condition) {
        ContractDiffTask task = new ContractDiffTask();
        task.setTaskName(condition.getTaskName());
        task.setStandardFileId(condition.getStandardFileId());
        task.setCompareFileId(condition.getCompareFileId());
        task.setProcessStatus(TaskProcessStatus.WAITING_PROCESS.code);
        task.setCreateTime(new Date());
        return task;
    }


}
