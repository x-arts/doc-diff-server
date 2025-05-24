package com.did.docdiffserver.controller;


import com.did.docdiffserver.compent.BaseApi;
import com.did.docdiffserver.data.base.ResponseContent;
import com.did.docdiffserver.data.condition.MdTextDiffCondition;
import com.did.docdiffserver.data.condition.TaskAddCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 合同比对接口
 * contract-diff
 */
@Slf4j
@RestController
public class ContractDiffTaskApi implements BaseApi {


    @PostMapping("/mdTextDiff")
    public ResponseContent mdTextDiff(@RequestParam String taskName,
                                      @RequestParam("standardFile") MultipartFile standardFile,
                                      @RequestParam("compareFile") MultipartFile compareFile) {


        return null;
    }



}
