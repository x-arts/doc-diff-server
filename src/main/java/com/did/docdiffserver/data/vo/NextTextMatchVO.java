package com.did.docdiffserver.data.vo;

import lombok.Data;

/**
 * @author xuewenke
 * @since 2025/5/16 00:25
 */
@Data
public class NextTextMatchVO {

    private String originalText;

    private String matchText;


    public static  NextTextMatchVO create(String originalText, String matchText) {
        NextTextMatchVO nextTextMatchVO = new NextTextMatchVO();
        nextTextMatchVO.setOriginalText(originalText);
        nextTextMatchVO.setMatchText(matchText);
        return nextTextMatchVO;
    }

    public boolean isNotSame() {
        return ! originalText.equals(matchText);
    }

}