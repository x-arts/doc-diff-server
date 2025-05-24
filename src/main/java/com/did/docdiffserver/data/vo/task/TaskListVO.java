package com.did.docdiffserver.data.vo.task;

import com.did.docdiffserver.data.entity.ContractDiffTask;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TaskListVO {


    private String taskName;

    private String taskId;

    private String difScore;

    private String status;

    private String createTime;


    public static TaskListVO of(ContractDiffTask contractDiffTask) {
        TaskListVO vo = new TaskListVO();
        vo.setTaskName(contractDiffTask.getTaskName());
        vo.setTaskId(contractDiffTask.getTaskId());
//        vo.setDifScore(contractDiffTask.getDifScore());
        vo.setStatus(contractDiffTask.getProcessStatus());
        vo.setCreateTime(contractDiffTask.getCreateTime().toString());
        return vo;
    }

    public static List<TaskListVO> of(List<ContractDiffTask> contractDiffTask) {
      return contractDiffTask.stream()
              .map(TaskListVO::of)
              .collect(Collectors.toList());
    }


}
