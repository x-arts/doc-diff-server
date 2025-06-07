package com.did.docdiffserver.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.did.docdiffserver.data.vo.SimilarSearchResult;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import toolgood.words.StringSearch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DocDiffUtils {


    public static String findMatchText(String dict, String findKey) {
        List<String> findText = preciseSearch(dict, findKey);

        if (CollectionUtil.isNotEmpty(findText)) {
            return findText.get(0);
        }

        // 相似度匹配
        List<SimilarSearchResult> searchResults = similarSearch(dict, findKey);
        Optional<SimilarSearchResult> max = searchResults.stream().max(Comparator.comparingDouble(SimilarSearchResult::getScore));
        if (max.isPresent()) {
            return max.get().getSimilarStr();
        }
        return "";
    }



    public static List<SimilarSearchResult>  similarSearch(String text, String key) {
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



    public static List<String> preciseSearch(String text, String key) {
        StringSearch search = new StringSearch();
        search.SetKeywords(CollectionUtil.newArrayList(key));
        return search.FindAll(text);
    }
}
