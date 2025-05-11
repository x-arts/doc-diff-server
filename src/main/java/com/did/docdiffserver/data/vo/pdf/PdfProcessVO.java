package com.did.docdiffserver.data.vo.pdf;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.utils.StrTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Data
@Slf4j
public class PdfProcessVO {

    private String fileId;

    private String filePath;


    /**
     * pdf 转换成 markdown 的文件内容
     */
    private List<String> mardDownList;


    /**
     *  简化后了的，用来对对比的列表
     */
    private List<String> simpleCompareList;


    public void  process() {
        buildMarkDownList(this.filePath);
        buildSimpleCompareList();
    }


    public static PdfProcessVO init(String filePath, String fileId) {
        PdfProcessVO vo = new PdfProcessVO();
        vo.filePath = filePath;
        vo.fileId = fileId;
        return vo;
    }

    /**
     * 获取 markdown 的内容
     *
     * @param filePath
     */
    private void buildMarkDownList(String filePath) {
        List<String> lines = FileUtil.readLines(filePath, "utf-8");
        this.mardDownList.addAll(lines);
    }


    /**
     * 数据预处理，移除 html  标签
     */
    private void buildSimpleCompareList() {
        List<String> list = new LinkedList<>();

        for (String line : this.mardDownList) {
            if (StrUtil.isBlank(line)) {
                continue;
            }

            if (StrTools.startsWithHtmlTag(line)) {
                continue;
            }
            //  目的是把目录去掉。单这个方式不一定精准
            if (line.matches("^\\d+.*$")) {
                continue;
            }
            String addLine = line.trim();
            if (addLine.startsWith("#")) {
                addLine = addLine.replaceAll("#", "");
            }

            // 去除行内的空格
            addLine = StrTools.removeSpaceInLine(addLine);

            // 去除标点符号
            addLine = StrTools.replacePunctuation(addLine);
            list.add(addLine);
        }
        this.simpleCompareList = list;
    }


}
