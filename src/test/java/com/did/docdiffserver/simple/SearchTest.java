package com.did.docdiffserver.simple;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.junit.Test;
import toolgood.words.StringSearch;

import java.util.List;

/**
 * @author xuewenke
 * @since 2025/5/7 00:40
 */
public class SearchTest {


    @Test
    public void simpleTest() {
        StringSearch search = new StringSearch();
        search.SetKeywords(CollectionUtil.newArrayList("国人水电的费大法"));

        String text = "我是中国人水电费大法师师傅是啊开房间撒发烧放假撒是啊放假啊手机放哥啊顺丰啊啥的发烧地方链接沙发上沙发上东风风神";
//        boolean contains = search.ContainsAny(text); // 是否包含任意关键词
//        System.out.println("是否包含敏感词: " + contains);

        List<String> found = search.FindAll(text); // 查找所有匹配的关键词
        System.out.println("匹配到的关键词: " + found);
    }


    @Test
    public void simpleTest2() {

        String A = "我是中国人水电费大法师师傅是啊开房间撒发烧放假撒是啊放假啊手机放哥啊顺丰啊啥的发烧地方链接沙发上沙发上东风风神";  // 待搜索的长字符串（长度 <= 10000）
        String B = "国人水电的费大法";  // 较小的模式串
        JaroWinklerSimilarity jw = new JaroWinklerSimilarity();
        double threshold = 0.85;  // 相似度阈值

        int lenB = B.length();
        // 遍历所有长度与 B 相同的滑动窗口子串
        for (int i = 0; i + lenB <= A.length(); i++) {
            String sub = A.substring(i, i + lenB);
            System.out.println("sub: " + sub);
            Double score = jw.apply(sub, B);
            if (score != null && score > threshold) {
                System.out.printf("Match at %d: \"%s\" (score=%.4f)%n", i, sub, score);
            }
        }

    }
}