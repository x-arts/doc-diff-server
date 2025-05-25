package com.did.docdiffserver.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.did.docdiffserver.compent.exception.BusinessException;
import com.did.docdiffserver.compent.exception.ErrorCode;
import com.did.docdiffserver.data.condition.TaskAddCondition;
import com.did.docdiffserver.data.condition.TaskPageListCondition;
import com.did.docdiffserver.data.entity.ContractDiffTask;
import com.did.docdiffserver.data.entity.ContractDiffTaskDetail;
import com.did.docdiffserver.data.enums.TaskProcessStatus;
import com.did.docdiffserver.data.vo.DiffResultItemVo;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.task.AddDiffTaskVo;
import com.did.docdiffserver.data.vo.task.DiffTaskPageListVO;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import com.did.docdiffserver.repository.ContractDiffTaskDetailRepository;
import com.did.docdiffserver.repository.ContractDiffTaskRepository;
import com.did.docdiffserver.service.compent.DocCovertService;
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
    private ContractDiffTaskDetailRepository diffTaskDetailRepository;

    @Resource
    private ListeningExecutorService executorService;

    @Resource
    private DocCovertService docCovertService;

    @Resource
    private DocDiffService docDiffService;

    @Resource
    private WordService wordService;

    @Resource
    private PdfService pdfService;



    public void getTaskDetail(String taskId) {
        ContractDiffTask task = diffTaskRepository.findByTaskId(taskId);
        if (task.getProcessStatus().equals(TaskProcessStatus.PROCESS_FAIL.code)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.code, "对比任务处理失败");
        }

        if (task.getProcessStatus().equals(PROCESSING.code)) {
            throw new BusinessException(ErrorCode.ILLEGAL_NOT_ASSERT.code, "对比任务处理中，请稍后再试");
        }

        ContractDiffTaskDetail detail = diffTaskDetailRepository.findByRelTaskId(task.getId());
        String compareResult = detail.getCompareResult();

        DiffResultItemVo diffResultItem = JSONObject.parseObject(compareResult, DiffResultItemVo.class);

    }


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
        return  DiffTaskPageListVO.of(contractDiffTaskIPage);
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
                // 文件的转换
                ContractDiffTaskDetail detail = docCovertService.docConventMarkdown(diffTask);

                WordProcessVO wordProcess = wordService.process(diffTask, detail);
                PdfProcessVO pdfProcess = pdfService.process(diffTask, detail, wordProcess);

                DiffResultItemVo diffResultItem = docDiffService.docDiffTask(wordProcess, pdfProcess);

                log.info("Diff  diffResultItem = {} ", JSONObject.toJSONString(diffResultItem));

                diffTask.setProcessStatus(TaskProcessStatus.PROCESS_SUCCESS.code);
                diffTaskRepository.updateById(diffTask);
            } catch (Exception e) {
                log.error("Error processing diff task {}", diffTask.getId(), e);
                diffTask.setProcessStatus(TaskProcessStatus.PROCESS_FAIL.code);
                diffTaskRepository.updateById(diffTask);
            }
        });

        diffTask.setProcessStatus(PROCESSING.code);
        return diffTask;
    }

}
