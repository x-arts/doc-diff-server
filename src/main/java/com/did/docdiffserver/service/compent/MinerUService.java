package com.did.docdiffserver.service.compent;

import com.did.docdiffserver.config.StoreConfig;
import com.did.docdiffserver.config.YamlConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class MinerUService {

    @Value("${local.file-upload-path}")
    private String uploadFilePath;

    @Resource
    private YamlConfig yamlConfig;

    @Resource
    private StoreConfig storeConfig;

    /**
     * 返回文件路径
     * @param filePath
     * @param fileId
     * @return
     */
    public String docToMarkdown(String filePath, String fileId) {

        String processMarkDownDir = storeConfig.getProcessMarkDownDir(fileId);
        convert(filePath, processMarkDownDir);
        String targetFileName = fileId + ".md";

        // 读取文件
        return storeConfig.getPdfMarkDownFilePath(fileId);
    }


    public void convert(String filePath, String outDir) {
        log.info("convert filePath = {}, outDir = {}", filePath, outDir);
        try {
            String scriptBasePath = yamlConfig.getScriptBasePath();
            String scriptPath = scriptBasePath + "mineru2md.sh";

            ProcessBuilder pb = new ProcessBuilder(scriptPath, filePath, outDir);
            pb.redirectErrorStream(true);  // 合并错误输出
            Process process = pb.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("MinerU doc2Markdown line = {}", line);
            }

            log.info("MinerUService convert  finish");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }


}
