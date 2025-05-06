package com.did.docdiffserver.simple;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.jsse.PEMFile;
import org.junit.Test;

import java.io.StringWriter;
import java.lang.reflect.Member;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class SimpleTest {

    private static final String filePath = "/Users/xuewenke/temp-file/diff-oneline/";

    @Test
    public void  simpleTest() {
        String wordMd2OneLinePath = wordMd2OneLine();
        String pdfMd2OneLinePath = pdfMd2OneLine();

        List<String> wordLines = FileUtil.readLines(wordMd2OneLinePath, "utf-8");
        List<String> pdfLines = FileUtil.readLines(pdfMd2OneLinePath, "utf-8");

        // 配置行内差异生成器
        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(false)
                .oldTag(f -> "[DEL]")  // 自定义旧文本标记
                .newTag(f -> "[INS]")  // 自定义新文本标记
                .build();

        // 比较单行文本
        List<DiffRow> rows = generator.generateDiffRows(
                CollectionUtil.newArrayList(String.join("",wordLines)) ,
                CollectionUtil.newArrayList(String.join("",pdfLines)));

        // 输出差异结果
        for (DiffRow row : rows) {
            System.out.println("旧行标记: " + row.getOldLine());
            System.out.println("新行标记: " + row.getNewLine());
        }

    }


    private String wordMd2OneLine() {
        String wordMdFilePath = filePath + "word-2.md";
        String outputFilePath = filePath + "word-out-2.md";
        if (!FileUtil.exist(outputFilePath)) {
            FileUtil.newFile(outputFilePath);
        }
        doMd2OneLine(wordMdFilePath, outputFilePath);
        return outputFilePath;
    }

    private String pdfMd2OneLine() {
        String mdFilePath = filePath + "pdf-2.md";
        String outputFilePath = filePath + "pdf-out-2.md";
        if (!FileUtil.exist(outputFilePath)) {
            FileUtil.newFile(outputFilePath);
        }
        doMd2OneLine(mdFilePath, outputFilePath);
        return outputFilePath;
    }


    private void doMd2OneLine(String inputFilePath, String outputFilePath) {
        List<String> mdLines = FileUtil.readLines(inputFilePath, "utf-8");
        List<String> newLines = new LinkedList<>();
        for (String mdLine : mdLines) {
            String addLine = mdLine.trim();
            addLine = addLine.replaceAll("\\s+", "");
            if (StrUtil.isBlank(mdLine)) {
                continue;
            }
            if (addLine.startsWith("#")){
                continue;
            }

            if (addLine.startsWith("<")) {
                continue;
            }

            if(addLine.matches("^[0-9].*")) {
                continue;
            }
            newLines.add(addLine);
        }
//        String oneLineMd = String.join("", newLines);
//        FileUtil.writeString(oneLineMd, outputFilePath, "utf-8");
        FileUtil.writeLines(newLines, outputFilePath, "utf-8");
    }

}
