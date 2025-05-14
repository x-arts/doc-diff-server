package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import com.did.docdiffserver.service.compent.StoreService;
import com.did.docdiffserver.utils.StrTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * word 文档的处理
 */
@Slf4j
@Service
public class WordService {


    @Resource
    private StoreService storeService;

    private static final List<String> filterKeyWords = new ArrayList<>();

    static {
        filterKeyWords.add("<div");
    }


    public String formatShowMarkdown(String filepath) {
        List<String> lines = FileUtil.readLines(filepath, StandardCharsets.UTF_8);
        // 移除没必要的行
        List<String> formatLines = filterLineForFormat(lines);

        // 表格合并
        formatLines = mergeHtmlContentOneLine(formatLines);
        FileUtil.writeLines(formatLines, filepath, StandardCharsets.UTF_8);
        String markdownContent = "";
        try {
            markdownContent  =  StreamUtils.copyToString(Files.newInputStream(Paths.get(filepath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            markdownContent = "# 解析失败";
            log.error(e.getMessage(), e);
        }
        return markdownContent;
    }

    private List<String> filterLineForFormat(List<String> lines) {
        return lines.stream().filter(line -> {
            for (String filterKeyWord : filterKeyWords) {
                if (line.startsWith(filterKeyWord)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * 把 html 的表格合成一行内容
     * @param lines
     * @return
     */
    private List<String>  mergeHtmlContentOneLine(List<String> lines) {
        List<String> result = new LinkedList<>();
        List<String> tableContent = new LinkedList<>();
        // 在收集 html 的标签
        boolean isCollecting = false;
        for (String line : lines) {
            if (StrTools.startsWithHtmlTag(line)) {
                tableContent.add(line);
                isCollecting = true;
                continue;
            }

            //不是 html 开始的内容
            if (isCollecting) {
                result.add(StrUtil.join("", tableContent));
                tableContent.clear();
                isCollecting = false;
                result.add(line);
            } else {
                result.add(line);
            }

        }
        return  result;
    }


    public WordProcessVO process(String filePath, String fileId) {
        /**
         * 1. docx 转 md 文档
         *
         */
        // 转换成 markdown
        String markdownFilePath = storeService.getWordMarkDownFilePath(fileId);
        log.info("wordProcess  markdownFilePath = {}", markdownFilePath);
        WordProcessVO wordProcessVO = WordProcessVO.init(fileId, filePath, markdownFilePath);
        wordProcessVO.buildCompareData();
        return wordProcessVO;
    }



}
