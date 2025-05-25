package com.did.docdiffserver.utils;

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
        System.out.println(StrTools.startsWithHtmlTag("</tr>"));
    }

}
