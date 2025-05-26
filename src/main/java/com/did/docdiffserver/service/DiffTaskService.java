package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.did.docdiffserver.compent.exception.BusinessException;
import com.did.docdiffserver.compent.exception.ErrorCode;
import com.did.docdiffserver.config.StoreConfig;
import com.did.docdiffserver.data.condition.TaskAddCondition;
import com.did.docdiffserver.data.condition.TaskPageListCondition;
import com.did.docdiffserver.data.entity.ContractDiffTask;
import com.did.docdiffserver.data.entity.ContractDiffTaskDetail;
import com.did.docdiffserver.data.entity.FileStore;
import com.did.docdiffserver.data.enums.TaskProcessStatus;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.task.AddDiffTaskVo;
import com.did.docdiffserver.data.vo.task.DiffTaskPageListVO;
import com.did.docdiffserver.data.vo.task.TaskCompareResultVO;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import com.did.docdiffserver.repository.ContractDiffTaskDetailRepository;
import com.did.docdiffserver.repository.ContractDiffTaskRepository;
import com.did.docdiffserver.repository.FileStoreRepository;
import com.did.docdiffserver.service.compent.DocCovertService;
import com.google.common.util.concurrent.ListeningExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

    @Resource
    private FileStoreRepository fileStoreRepository;

    @Resource
    private StoreConfig storeConfig;



    public TaskCompareResultVO getTaskDetail(String taskId) {
        ContractDiffTask task = diffTaskRepository.findByTaskId(taskId);
        if (task.getProcessStatus().equals(TaskProcessStatus.PROCESS_FAIL.code)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.code, "对比任务处理失败");
        }

        if (task.getProcessStatus().equals(PROCESSING.code)) {
            throw new BusinessException(ErrorCode.ILLEGAL_NOT_ASSERT.code, "对比任务处理中，请稍后再试");
        }

        ContractDiffTaskDetail detail = diffTaskDetailRepository.findByRelTaskId(task.getId());
        String compareResult = detail.getCompareResult();
        TaskCompareResultVO result = JSONObject.parseObject(compareResult, TaskCompareResultVO.class);


        FileStore wordMd = fileStoreRepository.findByFileId(result.getStdFileId());
        FileStore pdfMd = fileStoreRepository.findByFileId(result.getCmpFileId());

        try {
            String stdFileContent  =  StreamUtils.copyToString(Files.newInputStream(Paths.get(wordMd.getFilePath())), StandardCharsets.UTF_8);
            result.setStdFileContent(stdFileContent);
            String cmpFileContent  =  StreamUtils.copyToString(Files.newInputStream(Paths.get(pdfMd.getFilePath())), StandardCharsets.UTF_8);
            result.setCmpFileContent(cmpFileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
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

                TaskCompareResultVO diffResult = docDiffService.docDiffTask(wordProcess, pdfProcess);
                log.info("Diff  diffResult = {} ", JSONObject.toJSONString(diffResult));

                //  需要把打了 @标签 md 写入到两外一个文件里，用来回显用

                String baseDir = storeConfig.getShowMarkdownBasePath();
                List<String> wordMarkDownList = wordProcess.getMarkDownList();
                String fileId =  UUID.randomUUID().toString();
                String wordMdFilePath = baseDir + fileId +".md";
                FileStore localFile = FileStore.createLocalFile(fileId, wordMdFilePath, "MD");
                FileUtil.writeLines(wordMarkDownList, wordMdFilePath, StandardCharsets.UTF_8);
                fileStoreRepository.save(localFile);


                List<String> pdfMarkDownList = pdfProcess.getMardDownList();
                String pdfMdId =  UUID.randomUUID().toString();
                String pdfMdFilePath = baseDir + pdfMdId +".md";
                FileStore pdfLocalFile = FileStore.createLocalFile(pdfMdId, pdfMdFilePath, "MD");
                FileUtil.writeLines(pdfMarkDownList, pdfMdFilePath, StandardCharsets.UTF_8);
                fileStoreRepository.save(pdfLocalFile);


                diffResult.setStdFileId(fileId);
                diffResult.setCmpFileId(pdfMdId);


                detail.setCompareResult(JSONObject.toJSONString(diffResult));
                diffTaskDetailRepository.updateById(detail);


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
