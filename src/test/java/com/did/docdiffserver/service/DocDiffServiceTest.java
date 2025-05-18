package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.TestBase;
import com.did.docdiffserver.utils.HtmlUtils;
import com.did.docdiffserver.utils.StrTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DocDiffServiceTest extends TestBase {


    @Resource
    private DocDiffService docDiffService;

    @Resource
    private WordService wordService;

    @Resource
    private PdfService pdfService;



    @Test
    public void docDiffTest() {
        String wordFileId = "a91b1188-cc50-462e-952d-ad685abf9660";
        String pdfFileId = "b2b4a8f7-42b1-49f7-adc6-68d159573100";
        String diffStr = docDiffService.docDiff(wordFileId, pdfFileId);
    }

    @Test
    public void wordFormatTest() {
        String path = "/Users/xuewenke/doc-diff-server/process/markdown/a91b1188-cc50-462e-952d-ad685abf9660/a91b1188-cc50-462e-952d-ad685abf9660.md";
        wordService.formatShowMarkdown(path);
    }

    @Test
    public void pdfFormatTest() {
        String path = "/Users/xuewenke/doc-diff-server/process/markdown/b2b4a8f7-42b1-49f7-adc6-68d159573100/auto/b2b4a8f7-42b1-49f7-adc6-68d159573100.md";
        pdfService.formatShowMarkdown(path);
    }


    @Test
    public void mergeHtmlTableTest(){
        String pdfPath = "/Users/xuewenke/code/DID/web-server/doc-diff-server/src/main/temp-file/compare/table/pdf.md";
        String wordPath = "/Users/xuewenke/code/DID/web-server/doc-diff-server/src/main/temp-file/compare/table/word.md";
        List<String> readLines = FileUtil.readLines(pdfPath, StandardCharsets.UTF_8);
        List<String> wordMdLine = FileUtil.readLines(wordPath, StandardCharsets.UTF_8);


        Set<String> tableHeads = HtmlUtils.getTableHeads(wordMdLine);

//        List<String> collect = readLines.stream()
//                .filter(StrUtil::isNotBlank)
//                .collect(Collectors.toList());

        List<String> mergeHtmlTable = pdfService.mergeHtmlTable(readLines, tableHeads);

        List<String> tableLine = mergeHtmlTable.stream()
                .filter(line -> StrTools.isHtmlTable(line))
                .collect(Collectors.toList());

        System.out.println("mergeHtmlTable:" + tableLine.size());

//        mergeHtmlTable.forEach(System.out::println);
    }


}
