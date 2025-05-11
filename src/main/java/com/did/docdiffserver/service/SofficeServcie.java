package com.did.docdiffserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Service
public class SofficeServcie {

    @Value("${local.file-upload-path}")
    private String uploadFilePath;

    @Value("${local.script}")
    private String scriptBase;

    public boolean doc2Html(String filePath, String outDir) {

        try {
            System.out.println(scriptBase);
            String script = scriptBase + "convert_docx_to_html.sh";
            ProcessBuilder pb = new ProcessBuilder(script, filePath, outDir);
            pb.redirectErrorStream(true);  // 合并错误输出
            Process process = pb.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("doc2Html line = {}", line);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;

        }

    }


}
