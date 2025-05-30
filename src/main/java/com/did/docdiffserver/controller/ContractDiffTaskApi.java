package com.did.docdiffserver.controller;


import com.did.docdiffserver.compent.BaseApi;
import com.did.docdiffserver.data.base.ResponseContent;
import com.did.docdiffserver.data.condition.TaskAddCondition;
import com.did.docdiffserver.data.condition.TaskPageListCondition;
import com.did.docdiffserver.data.vo.task.AddDiffTaskVo;
import com.did.docdiffserver.data.vo.task.DiffTaskPageListVO;
import com.did.docdiffserver.data.vo.task.TaskCompareResultVO;
import com.did.docdiffserver.service.DiffTaskService;
import com.did.docdiffserver.service.store.FileLocalStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 合同比对接口
 * contract-diff
 */
@Slf4j
@RestController
public class ContractDiffTaskApi implements BaseApi {

    @Resource
    private FileLocalStoreService fileLocalStoreService;

    @Resource
    private DiffTaskService diffTaskService;


    @PostMapping("/api/contract-diff/create")
    public ResponseContent<AddDiffTaskVo> createDiffTask(@RequestParam String taskName,
                                      @RequestParam("standardFile") MultipartFile standardFile,
                                      @RequestParam("compareFile") MultipartFile compareFile) throws IOException {

        String standFileId = fileLocalStoreService.saveFile(standardFile);
        String compareFileId = fileLocalStoreService.saveFile(compareFile);
        TaskAddCondition condition = new TaskAddCondition();
        condition.setTaskName(taskName);
        condition.setStandardFileId(standFileId);
        condition.setCompareFileId(compareFileId);
        AddDiffTaskVo addDiffTaskVo = diffTaskService.addDiffTask(condition);

        return ResponseContent.success(addDiffTaskVo);
    }

    @PostMapping("/api/ctcdiff")
    public ResponseContent<DiffTaskPageListVO> diffTaskPageList(@RequestBody TaskPageListCondition condition)  {
        DiffTaskPageListVO pageList = diffTaskService.pageList(condition);
        return ResponseContent.success(pageList);
    }


    /**
     * 获取对比任务详情
     * @param taskId 任务ID
     * @return 对比任务详情
     *
     * @param taskId
     * @return
     */
    @GetMapping("/api/ctcdiff/detail")
    public ResponseContent<TaskCompareResultVO> getTaskCompareDetail(@RequestParam String taskId)  {
        TaskCompareResultVO taskDetail = diffTaskService.getTaskDetail(taskId);
        return ResponseContent.success(taskDetail);
    }


}
