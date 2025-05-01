package com.did.docdiffserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

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

    public String docToMarkdown(String filePath, String fileId) {
        convert(filePath);
        String targetFileName = fileId + ".md";

        // 读取文件
        String targetPath = uploadFilePath  + "mineru/" +  fileId + "/auto/" + targetFileName;

        try {
            return StreamUtils.copyToString(Files.newInputStream(Paths.get(targetPath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.info(e.getMessage(),e);
        }
        return "# 解析失败";

    }


    public void convert(String filePath) {
        log.info("convert filePath = {}", filePath);
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "/Users/xuewenke/temp-file/doc-diff-server/mineru2md.sh", filePath
            );
            List<String> command = pb.command();
            System.out.println(String.join(" ", command));
            pb.redirectErrorStream(true);  // 合并错误输出
            Process process = pb.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("MinerU doc2Markdown line = {}", line);
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }


}
