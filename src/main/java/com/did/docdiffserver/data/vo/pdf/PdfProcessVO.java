package com.did.docdiffserver.data.vo.pdf;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.base.Constant;
import com.did.docdiffserver.utils.StrTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@Slf4j
public class PdfProcessVO {

    private String fileId;

    private String markdownFilePath;


    /**
     * pdf 转换成 markdown 的文件内容
     */
    private List<String> mardDownList;


    /**
     *  简化后了的，用来对对比的列表
     */
    private List<String> compareMarkdownList;


    /**
     * 表格数据
     */
    private List<String> compareTableList = new ArrayList<>();



    /**
     * 用来比对的字典
     */
    private String compareDict;


    /**
     * 行的下标记录， 后续用来做二分查找
     */
    private List<Integer> lineIndex = new ArrayList<>();



    public static PdfProcessVO init(String fileId, String markdownFilePath) {
        PdfProcessVO vo = new PdfProcessVO();
        vo.markdownFilePath = markdownFilePath;
        vo.fileId = fileId;
        vo.mardDownList = new ArrayList<>();
        vo.compareMarkdownList = new ArrayList<>();
        return vo;
    }


    public void  buildCompareData() {
        buildMarkDownList(markdownFilePath);
        buildCompareMarkdownList();
        buildDict();
        buildLineIndex();
    }


    /**
     * 获取 markdown 的内容
     *
     * @param filePath
     */
    private void buildMarkDownList(String filePath) {
        List<String> lines = FileUtil.readLines(filePath, StandardCharsets.UTF_8);
        this.mardDownList.addAll(lines);
    }


    /**
     * 数据预处理，移除 html  标签
     */
    private void buildCompareMarkdownList() {
        for (String markdownLine : this.mardDownList) {
            String cleanLine = markdownLine.trim();
            // 移除行内的空格
            cleanLine  = StrTools.removeSpaceInLine(cleanLine);

            if (StrUtil.isBlank(cleanLine)) {
                this.compareMarkdownList.add(Constant.EMPTY_LINE);
                continue;
            }

            if (StrTools.startsWithHtmlTag(cleanLine)) {
                this.compareTableList.add(cleanLine);
                continue;
            }

            this.compareMarkdownList.add(cleanLine);
        }
    }


    private void  buildDict(){
        this.compareDict = String.join("", this.compareMarkdownList);
    }

    private void buildLineIndex() {
        int index = 0;
        for (String line : this.compareMarkdownList) {
            lineIndex.add(index);
            index += line.length();
        }
    }


}
