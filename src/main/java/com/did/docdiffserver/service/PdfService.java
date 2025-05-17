package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
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
        List<String> lines = new ArrayList<>();


        int waitMergeTableIndex = -1;
        boolean findNextTable = false;
        Element waitMergeTable = null;
        String waitMergeTableHeadLine = null;

        List<Integer> deleteIndex = new ArrayList<>();

        int findNextTableBlankCount = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (findNextTable) {
                if (StrUtil.isBlank(line)) {
                    findNextTableBlankCount++;
                }
                continue;
            }

            if (StrTools.isHtmlTable(line)) {
                Element table = Jsoup.parse(line).outputSettings(settings)
                        .select("table").first();
                String headLine = TableInfo.getTableHeadLine(table);
                log.info("headLine:{}", headLine);
                if (!findNextTable) {
                    // 没有在找到表格的情况下， 先报自己标记上
                    waitMergeTableIndex = i;
                    findNextTable = true;
                    waitMergeTable = table;
                    waitMergeTableHeadLine = headLine;
                    continue;
                }

                //  表示等待 merge  表格
                /**
                 * 表示在找表格，
                 *
                 * 表格合并的逻辑:
                 * 1、我的表格必须相邻，也就是说，下标是连续的。
                 * 2、标头是相同的的。
                 */
                boolean isMatchHeadline = waitMergeTableHeadLine.equals(headLine);
                boolean isNextLine = (i == waitMergeTableIndex + findNextTableBlankCount + 1);

                if (isMatchHeadline && isNextLine) {
                    Element mergeTable = tableInfoBuilder.mergeTable(waitMergeTable, table);
                    lines.set(waitMergeTableIndex,mergeTable.outerHtml());
                    deleteIndex.add(i);
                    findNextTableBlankCount++;
//                    findNextTable = false;
                } else {
                    waitMergeTableIndex = i;
                    waitMergeTable = table;
                    waitMergeTableHeadLine = headLine;
                }
            } else {
                if (findNextTable) {
                    findNextTable = false;
                }
            }
        }

        for (int index : deleteIndex) {
            lines.remove(index);
        }
        return lines;
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
