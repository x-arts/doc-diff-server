package com.did.docdiffserver.data.vo.word;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.base.Constant;
import com.did.docdiffserver.data.vo.CompareData;
import com.did.docdiffserver.utils.StrTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class WordProcessVO {

    /**
     * 比对最小的字符串长度
     * 如果一样小于 10，则需要加上下一行去比对
     */
    private static final int min_size = 10;


    /**
     * 用来表示是结束的行
     */
    public static final String END_LINE = "END_LINE";

    /**
     * word 文档的 Id
     */
    private String fileId;

    /**
     * markdown 文件路径
     */
    private String markDownFilePath;


    /**
     * docx 转换成 markdown 的文件内容
     */
    private List<String> markDownList = new ArrayList<>();


    /**
     * 用来比较的 markdown 文件内容
     * 会做数据清洗
     */
    private List<String> compareMarkdownList = new ArrayList<>();


    /**
     * 表格的文本数据
     */
    private List<String> compareTableList = new ArrayList<>();


    /**
     * 当前用来比对差异的 markdown 文本
     */
    public String currentCompareText;


    /**
     * 组成比对的文本 行数
     */
    private List<Integer> currentCompareTextLineNumbers = new ArrayList<>();


    /**
     * 当前下标
     */
    private int currentLineNum = -1;


    public static WordProcessVO init(String fileId, String markDownFilePath) {
        WordProcessVO vo = new WordProcessVO();
        vo.fileId = fileId;
        vo.markDownFilePath = markDownFilePath;
        return vo;
    }

    public void buildCompareData() {
        // 读取文档
        buildMarkDownList();

        // 文本清洗和构建
        buildCompareMarkdownList();
    }


    private void buildMarkDownList() {
        List<String> lines = FileUtil.readLines(markDownFilePath, StandardCharsets.UTF_8);
        this.markDownList.addAll(lines);
    }


    /**
     * @return
     */
    public CompareData fetchCompareText() {
        // 重置数据
        currentCompareTextLineNumbers.clear();
        currentCompareText = "";
        currentLineNum++;

        if (currentLineNum >= compareMarkdownList.size()) {
            return new CompareData(END_LINE, new ArrayList<>());
        }

        currentCompareText = getOneCompareText();
        if (currentCompareText.length() > min_size) {
            List<Integer> lineNumbers = new ArrayList<>(this.currentCompareTextLineNumbers);
            return new CompareData(currentCompareText, lineNumbers);
        }

        while (currentCompareText.length() < min_size) {
            currentLineNum++;
            String oneCompareText = getOneCompareText();
            currentCompareText = currentCompareText + oneCompareText;
            if (currentCompareText.length() > min_size) {
                break;
            }
        }
        List<Integer> lineNumbers = new ArrayList<>(this.currentCompareTextLineNumbers);
        return new CompareData(currentCompareText, lineNumbers);
    }


    /**
     * 获取当前行的比对文本
     *
     * @return
     */
    private String getOneCompareText() {
        log.info("getOneCompareText currentLineNum = {}", currentLineNum);
        if (currentLineNum >= compareMarkdownList.size()) {
            return END_LINE;
        }
        String compareText = compareMarkdownList.get(currentLineNum);
        // 遇到空行跳过
        if (compareText.equals(Constant.EMPTY_LINE)) {
            this.currentLineNum++;
            return getOneCompareText();
        }

        // 遇到表格跳过
        if (compareText.equals(Constant.HTML_LINE)) {
            this.currentLineNum++;
            return getOneCompareText();
        }

        // 遇到标题跳过
        if (compareText.startsWith("#")) {
            this.currentLineNum++;
            return getOneCompareText();
        }

        currentCompareTextLineNumbers.add(currentLineNum);
        return compareText;
    }


    private void buildCompareMarkdownList() {
        for (String markdownLine : this.markDownList) {
            String cleanLine = markdownLine.trim();
            // 移除行内的空格
            cleanLine = StrTools.removeSpaceInLine(cleanLine);

            if (StrUtil.isBlank(cleanLine)) {
                this.compareMarkdownList.add(Constant.EMPTY_LINE);
                continue;
            }

            if (StrTools.isHtmlTable(cleanLine)) {
                this.compareMarkdownList.add(Constant.HTML_LINE);
                this.compareTableList.add(cleanLine);
                continue;
            }

            if (StrTools.startsWithHtmlTag(cleanLine)) {
                this.compareMarkdownList.add(Constant.HTML_LINE);
                this.compareTableList.add(cleanLine);
                continue;
            }


            this.compareMarkdownList.add(cleanLine);
        }
    }


}
