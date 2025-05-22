package com.did.docdiffserver.data.vo.pdf;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.utils.StrTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class PdfProcessVO {

    /**
     * pdf 文件 Id
     */
    private String fileId;

    /**
     * markdown 文件路径
     */
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
     * 表格的文本数据
     */
    private List<String> compareTableList = new ArrayList<>();


    /**
     * 表格数据转换成表格对象
     */
    private List<TableInfo> tableInfoList = new ArrayList<>();


    /**
     * 用来比对的字典，全量
     */
    private String compareDict;

    /**
     * 行的下标记录， 后续用来做二分查找
     */
    private ArrayList<Integer> compareDictIndex = new ArrayList<>();


    /**
     * 动态字典，不断缩小的字典。
     */
    private String dynamicDict;


//    /**
//     * 行的下标记录， 后续用来做二分查找
//     */
//    private List<Integer> lineIndex = new ArrayList<>();


    private int cutIndex;


    /**
     * 获取匹配中的字符串的 起始下标
     * @return
     */
    public int getMatchTextIndex(int  startIndex, String matchText) {
        log.info("getMatchTextIndex: {}", matchText);
        int index = this.dynamicDict.indexOf(matchText, startIndex);
        log.info("getMatchTextIndex: index = {}", index);
        if (index == -1) {
            return -1;
        }
        return index;
    }

    public String getDictSubString(int start, int end) {
        log.info("startIndex: {}, endIndex: {}", start, end);
        this.cutIndex = start;
        return this.dynamicDict.substring(start, end);
    }

    /**
     * 缩小字典的范围
     * @param startIndex
     */
    public void  cutDynamicDict(int startIndex) {


    }





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
        buildTableInfo();
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
     *
     *  这里不保留完成的行数， 保留了，定位不出来具体的行数
     *  所以，就直接跳过 标题， 表格， 空行
     */
    private void buildCompareMarkdownList() {
        for (String markdownLine : this.mardDownList) {
            String cleanLine = markdownLine.trim();

            // 跳过表格
            if (StrTools.isHtmlTable(cleanLine)) {
                this.compareTableList.add(cleanLine);
                continue;
            }


            // 跳过空行
            if (StrUtil.isBlank(cleanLine)) {
//                this.compareMarkdownList.add(Constant.EMPTY_LINE);
                continue;
            }

            //跳过标题
            if (markdownLine.startsWith("#")) {
                continue;
            }


            // 移除行内的空格
            cleanLine  = StrTools.removeSpaceInLine(cleanLine);

            this.compareMarkdownList.add(cleanLine);
        }
    }


    /**
     * 构建大串的时候，还是需要把 空行， html # 过滤掉，
     *  因为在 word 哪里不是不会拼接空行，html 的
     */
    private void  buildDict(){
//        this.compareDict = String.join("", this.compareMarkdownList);
//        this.dynamicDict = compareDict;
        int currentIndex = 0;
        StringBuilder compareDictBuilder = new StringBuilder();
        for (int i = 0; i < compareMarkdownList.size(); i++) {
            String contentText = compareMarkdownList.get(i);
            compareDictBuilder.append(contentText);
            /**
             * 数组需要存的下标
             */
//            int maxIndex = currentIndex + contentText.length();
            for (char c : contentText.toCharArray()) {
                compareDictIndex.add(i);
            }
        }

        this.compareDict = compareDictBuilder.toString();
        this.dynamicDict = compareDict;
    }

    /**
     * 构建表格对象
     */
    private void buildTableInfo() {
        this.tableInfoList = TableInfo.buildTableInfoList(compareTableList);
    }


}
