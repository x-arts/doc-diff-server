package com.did.docdiffserver.service;

import cn.hutool.core.util.RuntimeUtil;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class MdDiffService {


    @Value("${local.file-upload-path}")
    private String uploadFilePath;

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
        String docFilePath = uploadFilePath + fileId + ".docx";
        String targetPath = uploadFilePath + fileId + "-docx.md";

        String cmd = "pandoc " + docFilePath + " -o " + targetPath;
        log.info("pandoc2md  cmd: {}", cmd);
        String str = RuntimeUtil.execForStr(cmd);
        log.info("pandoc2md  str: {}", str);
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
        String targetPath = uploadFilePath + fileId + "-pdf.md";
        String cmd = "marker_single --input " + pdfFilePath + " --output " + targetPath;
        log.info("pdf2md  cmd: {}", cmd);
        String str = RuntimeUtil.execForStr(cmd);
        log.info("pdf2md  str: {}", str);
        String content = "";
        try {
            content = StreamUtils.copyToString(Files.newInputStream(Paths.get(targetPath)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return content;
    }


    public String mdTextDiff(String oldText, String newText) {
        log.info("mdTextDiff: oldText={}, newText={}", oldText, newText);
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

        String unifiedDiffString = String.join(System.lineSeparator(), unifiedDiff);
        log.info("unifiedDiffString: {}", unifiedDiffString);

        return unifiedDiffString;
    }


}
