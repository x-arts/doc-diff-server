package com.did.docdiffserver.service;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class MdDiffService {

    @Value("${local.file-upload-path}")
    private String uploadFilePath;

    @Resource
    private MinerUService minerUService;



    public String mdTextDiff(String oldText, String newText) {
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
                3                 // 上下文行数
        );
        // 将 List<String> 转换为单个字符串

        //        log.info("unifiedDiffString: {}", unifiedDiffString);

        return String.join(System.lineSeparator(), unifiedDiff);
    }


}
