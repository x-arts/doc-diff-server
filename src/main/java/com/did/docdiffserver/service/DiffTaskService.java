package com.did.docdiffserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.did.docdiffserver.data.condition.TaskAddCondition;
import com.did.docdiffserver.data.condition.TaskPageListCondition;
import com.did.docdiffserver.data.entity.ContractDiffTask;
import com.did.docdiffserver.data.vo.task.AddDiffTaskVo;
import com.did.docdiffserver.data.vo.task.DiffTaskPageListVO;
import com.did.docdiffserver.repository.ContractDiffTaskRepository;
import com.google.common.util.concurrent.ListeningExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.did.docdiffserver.data.enums.TaskProcessStatus.PROCESSING;

@Slf4j
@Service
public class DiffTaskService {

    @Resource
    private ContractDiffTaskRepository diffTaskRepository;

    @Resource
    private ListeningExecutorService executorService;


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

        // 处理对比任务
        diffTask  = processDiffTask(diffTask);

        diffTaskRepository.save(diffTask);
        return AddDiffTaskVo.of(diffTask);
    }

    public DiffTaskPageListVO pageList(TaskPageListCondition condition) {
        IPage<ContractDiffTask> contractDiffTaskIPage = diffTaskRepository.pageList(condition);
        return   DiffTaskPageListVO.of(contractDiffTaskIPage);
    }

    /**
     * 处理对比任务
     *
     * @param diffTask 对比任务
     */
    public ContractDiffTask processDiffTask(ContractDiffTask diffTask) {
        log.info("Processing diff task: {}", diffTask.getId());
        // 这里可以添加实际的对比逻辑
        // 使用 executorService 提交任务到线程池
        executorService.submit(() -> {
            // 模拟对比处理
            try {
                Thread.sleep(2000); // 模拟耗时操作
                log.info("Diff task {} processed successfully", diffTask.getId());
            } catch (InterruptedException e) {
                log.error("Error processing diff task {}", diffTask.getId(), e);
            }
        });

        diffTask.setProcessStatus(PROCESSING.code);
        return diffTask;
    }

}
