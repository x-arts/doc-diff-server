package com.did.docdiffserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class SofficeServcie {



    @Value("${local.file-upload-path}")
    private String uploadFilePath;

    public String doc2Html(String filePath, String fileId) {
        /*
        soffice --headless --convert-to html:"HTML (StarWriter)" resume.docx  --outdir ./output
        -outdir /Users/xuewenke/temp-file/doc-diff-server
         */
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "/Users/xuewenke/temp-file/doc-diff-server/docx2html.sh", filePath
            );
            List<String> command = pb.command();
            System.out.println(String.join(" ", command));
            pb.redirectErrorStream(true);  // 合并错误输出
            pb.environment().put("HOME", "/tmp");
            Process process = pb.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("doc2Html line = {}", line);
            }

            //  读取文件
            String targetPath = uploadFilePath  + "html/" + fileId + ".html";
            return StreamUtils.copyToString(Files.newInputStream(Paths.get(targetPath)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        }
        return "";
    }


}
