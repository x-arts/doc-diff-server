package com.did.docdiffserver.data.vo.task;

import cn.hutool.core.date.DateUtil;
import com.did.docdiffserver.data.base.BaseVO;
import com.did.docdiffserver.data.entity.ContractDiffTask;
import lombok.Data;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_MINUTE_PATTERN;

@Data
public class AddDiffTaskVo implements BaseVO {

    private String taskId;
    private String id;
    private String taskName;
    private String status;
    private String createTime;


    public static AddDiffTaskVo of(ContractDiffTask task) {
        AddDiffTaskVo vo = new AddDiffTaskVo();
        vo.setId(String.valueOf(task.getId()));
        vo.setTaskId(task.getTaskId());
        vo.setTaskName(task.getTaskName());
        vo.setStatus(task.getProcessStatus());
        vo.setCreateTime(DateUtil.format(task.getCreateTime(), NORM_DATETIME_MINUTE_PATTERN));
        return vo;
    }

}
