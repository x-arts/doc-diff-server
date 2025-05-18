package com.did.docdiffserver.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.table.HtmlTableContent;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.service.table.TableInfoBuilder;
import com.did.docdiffserver.utils.StrTools;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * pdf 文档处理
 */
@Slf4j
@Service
public class PdfService {


    @Resource
    private FormatService formatService;


    @Resource
    private TableInfoBuilder tableInfoBuilder;

    Document.OutputSettings settings = new Document.OutputSettings()
            .prettyPrint(false)  // 禁用格式化
            .indentAmount(0);    // 无缩进


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

        formatLines = mergeHtmlTable(formatLines);

        FileUtil.writeLines(formatLines, filepath, StandardCharsets.UTF_8);
        String markdownContent = "";
        try {
            markdownContent = StreamUtils.copyToString(Files.newInputStream(Paths.get(filepath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            markdownContent = "# 解析失败";
            log.error(e.getMessage(), e);
        }
        return markdownContent;
    }


    public List<String> mergeHtmlTable(List<String> formatLines) {
        // 获取到 markdown 的表格， 有字符分割也打上了标签
        List<HtmlTableContent> htmlTableContents = buildMergeTableList(formatLines);
        return Collections.emptyList();
    }


    private List<HtmlTableContent>  buildMergeTableList(List<String> formatLines) {
        List<HtmlTableContent> result = new ArrayList<>();
        for (int i = 0; i < formatLines.size(); i++) {
            String line = formatLines.get(i);
            line = line.trim();
            if (StrTools.isHtmlTable(line)) {
                HtmlTableContent htmlTableContent = new HtmlTableContent(line, i);
                addHtmlTableContent(result, htmlTableContent);
                continue;
            }

            if (StrUtil.isNotBlank(line)) {
                System.out.println("line = " + line);
                addHtmlTableContent(result, HtmlTableContent.ofSplit());
            }
        }

        return result;
    }



    private void  addHtmlTableContent(List<HtmlTableContent> list, HtmlTableContent htmlTableContent) {
        System.out.println("addHtmlTableContent");
        if (htmlTableContent.isTable()){
            list.add(htmlTableContent);
        } else {
            if (CollectionUtil.isEmpty(list)) {
                return;
            }

            int lastIndex =  list.size() -1;
            HtmlTableContent lastOne = list.get(lastIndex);
            if (lastOne.isTable()) {
                list.add(htmlTableContent);
            }
        }

    }


    /**
     * 找页眉页脚
     *
     * @return
     */
    private Set<String> findPageTitle(List<String> lines) {

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
            if (count < matchCount) {
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
