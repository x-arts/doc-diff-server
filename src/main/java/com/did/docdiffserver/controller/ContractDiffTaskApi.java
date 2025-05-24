package com.did.docdiffserver.controller;


import com.did.docdiffserver.compent.BaseApi;
import com.did.docdiffserver.data.base.ResponseContent;
import com.did.docdiffserver.data.condition.TaskAddCondition;
import com.did.docdiffserver.data.condition.TaskPageListCondition;
import com.did.docdiffserver.data.vo.task.AddDiffTaskVo;
import com.did.docdiffserver.data.vo.task.DiffTaskPageListVO;
import com.did.docdiffserver.service.DiffTaskService;
import com.did.docdiffserver.service.store.FileLocalStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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


        return ResponseContent.success();
    }



}
