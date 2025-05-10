package com.did.docdiffserver.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.SimilarSearchResult;
import com.did.docdiffserver.utils.StrTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;
import toolgood.words.StringSearch;

import java.util.*;

@Slf4j
@Service
public class RevisedPdfFindDiffService {

    private static final String localTempFilePath = "/Users/xuewenke/code/DID/web-server/doc-diff-server/src/main/temp-file/";
//    private static final String localTempFilePath =  "/Users/xuewenke/workspace/code/doc-diff-server/src/main/temp-file/";




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
        for (String string : found) {
            log.info("preciseSearch match word = {}", string);
        }
        return true;
    }




}
