package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


/**
 *  pdf 文档处理
 */
@Slf4j
@Service
public class PdfService {


    @Resource
    private FormatService formatService;


    public PdfProcessVO process(String markdownFilePath, String fileId) {
        PdfProcessVO processVO = PdfProcessVO.init(fileId, markdownFilePath);
        processVO.buildCompareData();
        return processVO;
    }



    public String formatShowMarkdown(String filepath) {
        List<String> lines = FileUtil.readLines(filepath, StandardCharsets.UTF_8);

        Set<String> pageTitle = findPageTitle(lines);
        // 移除没必要的行
        List<String> formatLines = filterLineForFormat(lines, pageTitle);

        formatLines = formatService.symbolicFormat(formatLines);

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




    /**
     * 找页眉页脚
     * @return
     */
    private  Set<String> findPageTitle(List<String> lines) {

        int matchCount = 3;

        Set<String> pageTitle = new HashSet<>();

        Map<String, Integer> matchMap = new HashMap<>();

        for (String line : lines) {
            if (StrUtil.isBlank(line)) {
                continue;
            }
            Integer count = matchMap.get(line);
            if (count == null) {
                matchMap.put(line, 1);
                continue;
            }
            if (count<matchCount) {
                count++;
                matchMap.put(line, count);
                continue;
            }
            pageTitle.add(line);
            count++;
            matchMap.put(line, count);
        }

        return pageTitle;
    }


    private List<String> filterLineForFormat(List<String> lines, Set<String> filterKeyWords) {
        return lines.stream().filter(line -> {
            for (String filterKeyWord : filterKeyWords) {
                if (line.startsWith(filterKeyWord)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }



}
