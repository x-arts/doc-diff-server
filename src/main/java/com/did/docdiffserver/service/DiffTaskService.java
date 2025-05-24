package com.did.docdiffserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.did.docdiffserver.data.condition.TaskAddCondition;
import com.did.docdiffserver.data.condition.TaskPageListCondition;
import com.did.docdiffserver.data.entity.ContractDiffTask;
import com.did.docdiffserver.data.vo.task.AddDiffTaskVo;
import com.did.docdiffserver.data.vo.task.DiffTaskPageListVO;
import com.did.docdiffserver.repository.ContractDiffTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class DiffTaskService {

    @Resource
    private ContractDiffTaskRepository diffTaskRepository;



    /**
     * 添加对比任务
     *
     * @param condition 任务添加条件
     * @return 添加结果
     *
     */
    public AddDiffTaskVo addDiffTask(TaskAddCondition condition) {
        log.info("addDiffTask condition = {}", condition.toJsonStr());
        ContractDiffTask diffTask = ContractDiffTask.createAddOf(condition);
        diffTaskRepository.save(diffTask);

        return AddDiffTaskVo.of(diffTask);
    }

    public DiffTaskPageListVO pageList(TaskPageListCondition condition) {
        IPage<ContractDiffTask> contractDiffTaskIPage = diffTaskRepository.pageList(condition);

        return   DiffTaskPageListVO.of(contractDiffTaskIPage);
    }

}
