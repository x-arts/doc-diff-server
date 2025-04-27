package com.did.docdiffserver.controller;

import com.alibaba.fastjson2.JSONObject;
import com.did.docdiffserver.data.condition.MdTextDiffCondition;
import com.did.docdiffserver.service.MdDiffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/md-diff-api/")
public class MdDiffController {

    @Resource
    private MdDiffService mdDiffService;


    @PostMapping("/mdTextDiff")
    public String mdTextDiff(@RequestBody MdTextDiffCondition condition) {
        log.info("mdTextDiff: condition = {}", JSONObject.toJSONString(condition));
        return mdDiffService.mdTextDiff(condition.getOldText(), condition.getNewText());
    }

}
