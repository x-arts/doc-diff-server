package com.did.docdiffserver.utils;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrTools {

    // 正则表达式匹配HTML标签开头
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("^\\s*<\\/?[a-zA-Z][a-zA-Z0-9]*\\b[^>]*>");


    /**
     * 删除标点符号
     * @param text
     * @return
     */
    public static String replacePunctuation(String text) {
        return text.replaceAll("[\\p{P}\\p{S}]", "");
    }

    public static String removeSpaceInLine(String text) {
        return text.replaceAll("\\s+", "");
    }



    public static boolean startsWithHtmlTag(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        Matcher matcher = HTML_TAG_PATTERN.matcher(input);
        return matcher.find();
    }


    public static boolean isHtmlTable(String input) {
      return startsWithHtmlTag(input) && input.contains("table");
    }

    public static void main(String[] args) {



    }




    public static List<Integer> findAllIndices(String A, String B) {
        if (StrUtil.isBlank(A)) {
            return Collections.emptyList();
        }
        List<Integer> indices = new ArrayList<>();
        int index = A.indexOf(B); // 第一次查找

        while (index != -1) {
            indices.add(index);
            index = A.indexOf(B, index + 1); // 从下一个位置继续查找
        }

        return indices;
    }


}
