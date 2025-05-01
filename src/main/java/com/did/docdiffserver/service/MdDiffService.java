package com.did.docdiffserver.service;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class MdDiffService {

    @Value("${local.file-upload-path}")
    private String uploadFilePath;



    public String markDocxFile(String filePath, String fileId) {
        String html = doc2Html(filePath, fileId);
        Document document = Jsoup.parse(html);
        return document.body().html();
    }

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



    public String doc2md(String fileId, String suffix) {
        if (suffix.equals("docx")) {
            return pandoc2md(fileId);
        }

        if (suffix.equals("pdf")) {
            return pdf2md(fileId);
        }
        throw new RuntimeException("suffix not support");
    }

    public String pandoc2md(String fileId) {
        /*
        pandoc input.docx -f docx -t markdown --atx-headers -o output.md
        •	-f docx：输入格式是 docx
        •	-t markdown：输出成 markdown
        •	--markdown-headings=atx ：强制使用 # 号风格的标题（ATX 样式）
        •	-o output.md：输出的文件
        -f docx -t markdown  --markdown-headings=atx
         */
        String docFilePath = uploadFilePath + fileId + ".docx";
        String targetPath = uploadFilePath + fileId + "-docx.md";

        try {
//            FileChannel channel = FileChannel.open(Paths.get(docFilePath), StandardOpenOption.WRITE);
//            channel.force(true);

            ProcessBuilder pb = new ProcessBuilder(
                    "pandoc",
                    docFilePath,
                    "-f", "docx",
                    "-t", "markdown",
                    "--markdown-headings=atx",
                    "-o",
                    targetPath
            );
            pb.redirectErrorStream(true);  // 合并错误输出
            log.info("word 转 markdown 开始");
            Process process = pb.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("pandoc2md  str: {}", line);
            }
            log.info("word 转 markdown 结束");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String content = "";
        try {
            content = StreamUtils.copyToString(Files.newInputStream(Paths.get(targetPath)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }

        return content;
    }

    public String pdf2md(String fileId) {
        /*
         * marker_single --input /path/to/input.pdf --output /path/to/output.md --batch-size 2
         */
        String pdfFilePath = uploadFilePath + fileId + ".pdf";

        String outDir = uploadFilePath;
        String content = "";
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "marker_single", pdfFilePath, "--output_format", "markdown","--force_ocr", "--output_dir", outDir
            );
            pb.redirectErrorStream(true);  // 合并错误输出
            log.info("pdf 转 markdown 开始");
            Process process = pb.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("pdf2md  process-line: {}", line);
            }
            log.info("pdf 转 markdown 结束");

            String targetPath = uploadFilePath + fileId + "/" + fileId + ".md";
            log.info("pdf2md  targetPath: {}", targetPath);
            content = StreamUtils.copyToString(Files.newInputStream(Paths.get(targetPath)), StandardCharsets.UTF_8);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return content;
    }


    public String mdTextDiff(String oldText, String newText) {
//        log.info("mdTextDiff: oldText={}, newText={}", oldText, newText);
        return generateUnifiedDiff(oldText, newText, "old.md", "new.md");
    }


    /**
     * 生成标准 Unified Diff 格式文本
     *
     * @param original 原始文本
     * @param revised  修改后文本
     * @param fromFile 原始文件名（显示在---行）
     * @param toFile   修改文件名（显示在+++行）
     * @return diff格式字符串
     */
    public String generateUnifiedDiff(
            String original,
            String revised,
            String fromFile,
            String toFile) {

        // 分割文本为行列表
        List<String> originalLines = Arrays.asList(original.split("\\R"));
        List<String> revisedLines = Arrays.asList(revised.split("\\R"));

        // 生成差异补丁
        Patch<String> patch = DiffUtils.diff(originalLines, revisedLines);

        // 构建 Diff 输出
        StringWriter stringWriter = new StringWriter();


        // 2. 用 UnifiedDiffUtils 生成 unified diff 内容
        List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(
                "original.txt",   // 原文件名
                "revised.txt",    // 新文件名
                originalLines,         // 原内容
                patch,            // Patch对象
                30                 // 上下文行数
        );
        // 将 List<String> 转换为单个字符串

        //        log.info("unifiedDiffString: {}", unifiedDiffString);

        return String.join(System.lineSeparator(), unifiedDiff);
    }


}
