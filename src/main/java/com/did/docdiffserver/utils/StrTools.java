package com.did.docdiffserver.utils;

public class StrTools {

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
}
