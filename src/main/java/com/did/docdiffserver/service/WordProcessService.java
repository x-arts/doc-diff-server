package com.did.docdiffserver.service;

import com.did.docdiffserver.config.YamlConfig;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import com.did.docdiffserver.service.compent.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * word 文档的处理
 */
@Slf4j
@Service
public class WordProcessService {


    @Resource
    private YamlConfig yamlConfig;

    @Resource
    private StoreService storeService;

    public WordProcessVO wordProcess(String fileId) {
        /**
         * 1. docx 转 md 文档
         *
         */
        String filePath = yamlConfig.getUploadBase() + fileId + ".docx";
        WordProcessVO wordProcessVO = WordProcessVO.init(filePath, fileId);

        // 转换成 markdown
        String markdownFilePath = storeService.getProcessMarkDownFilePath(fileId);
        log.info("wordProcess  markdownFilePath = {}", markdownFilePath);
        wordProcessVO.buildMarkDownList(markdownFilePath);
        wordProcessVO.buildNoHtmlTagList();
        wordProcessVO.buildDict();

        return wordProcessVO;
    }



}
