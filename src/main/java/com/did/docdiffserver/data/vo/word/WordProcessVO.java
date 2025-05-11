package com.did.docdiffserver.data.vo.word;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.utils.StrTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Data
@Slf4j
public class WordProcessVO {

    private String fileId;

    private String filePath;


    /**
     * docx 转换成 markdown 的文件内容
     */
    private List<String> mardDownList;


    /**
     * 删除了表格的 markdown
     */
    private List<String> noHtmlTagList;

    /**
     * 用来比对的字典
     */
    private String compareDict;


    /*
      还需要回溯到行数
     */


    public static WordProcessVO init(String filePath, String fileId) {
        WordProcessVO vo = new WordProcessVO();
        vo.filePath = filePath;
        vo.fileId = fileId;
        return vo;
    }

    /**
     * 获取 markdown 的内容
     *
     * @param filePath
     */
    public void buildMardDownList(String filePath) {
        List<String> lines = FileUtil.readLines(filePath, "utf-8");
        this.mardDownList.addAll(lines);
    }


    /**
     * 数据预处理，移除 html  标签
     */
    public void buildNoHtmlTagList() {
        for (String line : this.mardDownList) {
            if (StrTools.startsWithHtmlTag(line)) {
                continue;
            }
            this.noHtmlTagList.add(line);
        }
    }


    public void buildDict() {
        List<String> dictLine = new LinkedList<>();
        for (String line : this.noHtmlTagList) {
            if (StrUtil.isBlank(line)) {
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
            dictLine.add(addLine);
        }
        this.compareDict = String.join("", dictLine);
    }
}
