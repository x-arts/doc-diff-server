package com.did.docdiffserver.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.table.HtmlTableContent;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.data.vo.table.TableMergeResult;
import com.did.docdiffserver.service.table.TableInfoBuilder;
import com.did.docdiffserver.utils.HtmlUtils;
import com.did.docdiffserver.utils.MergeTableUtils;
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



    public PdfProcessVO process(String markdownFilePath, String fileId) {
        PdfProcessVO processVO = PdfProcessVO.init(fileId, markdownFilePath);
        processVO.buildCompareData();
        return processVO;
    }


    public String formatShowMarkdown(String filepath, Set<String> standHeads) {
        List<String> lines = FileUtil.readLines(filepath, StandardCharsets.UTF_8);

        Set<String> pageTitle = findPageTitle(lines);
        // 移除没必要的行
        List<String> formatLines = filterLineForFormat(lines, pageTitle);

        formatLines = formatService.symbolicFormat(formatLines);

        formatLines = mergeHtmlTable(formatLines, standHeads);

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


    public List<String> mergeHtmlTable(List<String> formatLines, Set<String> standHeads) {
        // 获取到 markdown 的表格， 有字符分割也打上了标签
        List<HtmlTableContent> htmlTableContents = buildMergeTableList(formatLines);

        for (HtmlTableContent htmlTableContent : htmlTableContents) {
            if (htmlTableContent.isTable()) {
                // 移除识别错误导致的错误表头
                HtmlUtils.adjustTableHead(htmlTableContent, standHeads);
            }
        }

        // 表格合并结果
        TableMergeResult result = MergeTableUtils.mergeTable(htmlTableContents, standHeads);

//        List<String> mergeTableFormat = new ArrayList<>(formatLines);


        // 先修改表格，再删除行数，避免影响下标
        List<HtmlTableContent> reSetTables = result.getReSetTables();
        for (HtmlTableContent reSetTable : reSetTables) {
            formatLines.set(reSetTable.getIndex(), reSetTable.getHtml());
        }

        List<Integer> removeLineIndex = result.getRemoveLineIndex();
        int deleteCount = 0;
        for (int lineIndex : removeLineIndex) {
            // 每删除一行 都会影响下标，所以删除一行，对应
            formatLines.remove(lineIndex - deleteCount);
            deleteCount++;
        }

        formatLines = addAttrId(formatLines);

        return formatLines;
    }


    private List<String> addAttrId(List<String> formatLines) {

        List<HtmlTableContent> htmlTableContents =  new ArrayList<>();
        for (int i = 0; i < formatLines.size(); i++) {
            String line = formatLines.get(i);
            line = line.trim();
            if (StrTools.isHtmlTable(line)) {
                htmlTableContents.add(new HtmlTableContent(line, i));
            }
        }

        for (int i = 0; i < htmlTableContents.size(); i++) {
            HtmlTableContent reSetTable = htmlTableContents.get(i);
            i++;
            String html = HtmlUtils.addTableId(reSetTable.getHtml(), "table" + i);
            reSetTable.setHtml(html);
        }

        for (HtmlTableContent htmlTableContent : htmlTableContents) {
            formatLines.set(htmlTableContent.getIndex(), htmlTableContent.getHtml());
        }
        return formatLines;
    }

    //        int index = 1;

//        // 添加 id 属性
//        for (HtmlTableContent reSetTable : mergeResult.getReSetTables()) {
//            String html = HtmlUtils.addTableId(reSetTable.getHtml(), "table" + index);
//            reSetTable.setHtml(html);
//            index++;
//        }




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
                addHtmlTableContent(result, HtmlTableContent.ofSplit());
            }
        }

        return result;
    }



    private void  addHtmlTableContent(List<HtmlTableContent> list, HtmlTableContent htmlTableContent) {
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
