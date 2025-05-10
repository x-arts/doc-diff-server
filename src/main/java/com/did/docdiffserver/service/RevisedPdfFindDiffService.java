package com.did.docdiffserver.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.Cell;
import com.did.docdiffserver.data.vo.Row;
import com.did.docdiffserver.data.vo.SimilarSearchResult;
import com.did.docdiffserver.data.vo.TableInfo;
import com.did.docdiffserver.utils.StrTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;
import toolgood.words.StringSearch;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class RevisedPdfFindDiffService {

    private static final String localTempFilePath = "/Users/xuewenke/code/DID/web-server/doc-diff-server/src/main/temp-file/";
//    private static final String localTempFilePath =  "/Users/xuewenke/workspace/code/doc-diff-server/src/main/temp-file/";


    @Resource
    private TableService tableService;

    public void  pdfTableFindDiff(String dict, String pdfMdFilePath) {
        List<String> pdfLines = FileUtil.readLines(pdfMdFilePath, "utf-8");
        List<String> wordTableDiffs = new LinkedList<>();
        List<String> pdfTableDiffs = new LinkedList<>();
        for (String pdfTableDiff : pdfLines) {
//            System.out.println(pdfTableDiff);
            if (pdfTableDiff.startsWith("<html>") && pdfTableDiff.contains("table")) {
                TableInfo tableInfo = tableService.getTableInfo(pdfTableDiff);
                for (Row row : tableInfo.getRows()) {
                    for (Cell cell : row.getCells()) {
                        String cellText = StrTools.removeSpaceInLine(cell.getText());
                        cellText = StrTools.replacePunctuation(cellText);
                        findDiff(wordTableDiffs, pdfTableDiffs, dict, cellText);
                    }
                }
            }
        }

        for (String wordDiff : wordTableDiffs) {
            System.out.println(wordDiff);
        }
        System.out.println("=========================");
        for (String pdfDiff : pdfTableDiffs) {
            System.out.println(pdfDiff);
        }
    }


    public void findDiff(List<String> original, List<String> modify, String dict, String findKye) {
//        log.info("findDiff findKye = {}", findKye);
        if (StrUtil.isBlank(findKye)) {
            return;
        }
        boolean findResult = preciseSearch(dict, findKye);
//        log.info("findDiff findResult = {}",findKye);
        if (!findResult) {
            // 相似度匹配
            List<SimilarSearchResult> searchResults = similarSearch(dict, findKye);
            Optional<SimilarSearchResult> max = searchResults.stream().max(Comparator.comparingDouble(SimilarSearchResult::getScore));
            if (max.isPresent()) {
                original.add(max.get().getSimilarStr());
                modify.add(findKye);
            }
        }

    }


    public void  simplePdfMdFindDiff(String dict, String pdfMdFilePath) {
        List<String> pdfLines = FileUtil.readLines(pdfMdFilePath, "utf-8");

        List<String> wordDiffs = new LinkedList<>();
        List<String> pdfDiffs = new LinkedList<>();

        for (String pdfLine : pdfLines) {
            pdfLine = StrTools.replacePunctuation(pdfLine);
            boolean findResult = preciseSearch(dict, pdfLine);
//            log.info("simplePdfMdFindDiff   findResult = {}", findResult);
            if (!findResult) {
                // 相似度匹配
                List<SimilarSearchResult> searchResults = similarSearch(dict, pdfLine);
                Optional<SimilarSearchResult> max = searchResults.stream().max(Comparator.comparingDouble(SimilarSearchResult::getScore));
                if (max.isPresent()) {
                    wordDiffs.add(max.get().getSimilarStr());
                    pdfDiffs.add(pdfLine);
                }
            }
        }

        for (String wordDiff : wordDiffs) {
            System.out.println(wordDiff);
        }
        System.out.println("=========================");
        for (String pdfDiff : pdfDiffs) {
            System.out.println(pdfDiff);
        }

    }

    private  List<SimilarSearchResult>  similarSearch(String text, String key) {
        JaroWinklerSimilarity jw = new JaroWinklerSimilarity();
        double threshold = 0.85;  // 相似度阈值

        List<SimilarSearchResult> searchResults = new ArrayList<>();
        int lenB = key.length();
        // 遍历所有长度与 B 相同的滑动窗口子串
        for (int i = 0; i + lenB <= text.length(); i++) {
            String sub = text.substring(i, i + lenB);
            Double score = jw.apply(sub, key);
            if (score != null && score > threshold) {
                searchResults.add(new SimilarSearchResult(sub, score));
            }
        }
        return searchResults;
    }


    private boolean preciseSearch(String text, String key) {
        StringSearch search = new StringSearch();
        search.SetKeywords(CollectionUtil.newArrayList(key));
        List<String> found = search.FindAll(text);
        if (CollectionUtil.isEmpty(found)) {
            return false;
        }
//        for (String string : found) {
//            log.info("preciseSearch match word = {}", string);
//        }
        return true;
    }




}
