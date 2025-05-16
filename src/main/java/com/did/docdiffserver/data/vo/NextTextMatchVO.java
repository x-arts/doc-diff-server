package com.did.docdiffserver.data.vo;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * @author xuewenke
 * @since 2025/5/16 00:25
 */
@Data
public class NextTextMatchVO {

    private CompareData originalText;

    private String matchText;


    public static  NextTextMatchVO create(CompareData originalText, String matchText) {
        NextTextMatchVO nextTextMatchVO = new NextTextMatchVO();
        nextTextMatchVO.setOriginalText(originalText);
        nextTextMatchVO.setMatchText(matchText);
        return nextTextMatchVO;
    }

    public boolean isNotSame() {
        return ! originalText.getCompareText().equals(matchText);
    }


    public boolean isMatchTextEmpty() {
        return StrUtil.isBlank(matchText);
    }

}