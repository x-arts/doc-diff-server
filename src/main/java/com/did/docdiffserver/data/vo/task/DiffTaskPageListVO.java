package com.did.docdiffserver.data.vo.task;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.did.docdiffserver.data.base.BasePageVO;
import com.did.docdiffserver.data.entity.ContractDiffTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DiffTaskPageListVO extends BasePageVO {

    private List<TaskListVO> data;


    public static DiffTaskPageListVO of(IPage<ContractDiffTask> pageList) {
        DiffTaskPageListVO vo = new DiffTaskPageListVO();
        vo.setPage(pageList);
        List<ContractDiffTask> records = pageList.getRecords();
        List<TaskListVO> taskListVOS = TaskListVO.of(records);
        vo.setData(taskListVOS);
        return vo;
    }





}
