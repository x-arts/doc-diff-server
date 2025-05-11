package com.did.docdiffserver.service.compent;

import cn.hutool.core.io.FileUtil;
import com.did.docdiffserver.config.YamlConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xuewenke
 * @since 2025/5/11 23:38
 */
@Slf4j
@Service
public class StoreService {

    @Resource
    private YamlConfig yamlConfig;

    public String getProcessMarkDownDir(String fileId) {
        String dirOut = yamlConfig.getProcessBasePath() + "/markdown/" + fileId + "/";
        FileUtil.mkdir(dirOut);
        return dirOut;
    }

    public String getProcessMarkDownFilePath(String fileId) {
        String dirOut = yamlConfig.getProcessBasePath() + "/markdown/" + fileId + "/";
        return dirOut + fileId + ".md";
    }

}