package com.did.docdiffserver.config;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xuewenke
 * @since 2025/5/11 23:38
 */
@Slf4j
@Service
public class StoreConfig {

    @Resource
    private YamlConfig yamlConfig;


    public String getUploadBase() {
        return yamlConfig.getUploadBase();
    }

    public String getPdfFilePath(String fileId) {
        return yamlConfig.getUploadBase() + fileId + ".pdf";
    }


    public String getWordFilePath(String fileId) {
        return yamlConfig.getUploadBase() + fileId + ".docx";
    }

    public String getProcessMarkDownDir(String fileId) {
        String dirOut = yamlConfig.getMarkdownBasePath() + fileId + "/";
        FileUtil.mkdir(dirOut);
        return dirOut;
    }

    public String getWordMarkDownFilePath(String fileId) {
        String dirOut = yamlConfig.getMarkdownBasePath() + fileId + "/";
        return dirOut + fileId + ".md";
    }

    public String getPdfMarkDownFilePath(String fileId) {
        String dirOut = yamlConfig.getMarkdownBasePath() + fileId +"/" + fileId + "/auto/";
        return dirOut + fileId + ".md";
    }

}