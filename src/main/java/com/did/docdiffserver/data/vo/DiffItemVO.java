package com.did.docdiffserver.data.vo;

import cn.hutool.core.util.StrUtil;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class DiffItemVO {

    private String originalText;

    private String modifiedText;

    private List<Integer>  originalLineNumbers;

    private List<DiffItemDetailVO> diffDetail;


    public static  DiffItemVO create(String originalText, String modifiedText, List<Integer> originalLineNumbers) {
        DiffItemVO diffItemVO = new DiffItemVO();
        diffItemVO.originalText = originalText;
        diffItemVO.modifiedText = modifiedText;
        diffItemVO.originalLineNumbers = originalLineNumbers;
        return diffItemVO;
    }


    public void buildDiffDetail(){
        this.diffDetail = new ArrayList<>();
        if (StrUtil.isBlank(modifiedText)) {
            return;
        }
        List<String> originalChars = Arrays.asList(originalText.split(""));
        List<String> revisedChars = Arrays.asList(modifiedText.split(""));

        Patch<String> patch = DiffUtils.diff(originalChars, revisedChars);

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            DiffItemDetailVO detailVO = new DiffItemDetailVO();
            detailVO.setType(delta.getType().toString());
            detailVO.setOriginalStr(delta.getSource().toString());
            detailVO.setModifyStr(delta.getTarget().toString());
            detailVO.setOriginalStrPosition(delta.getSource().getPosition());
            detailVO.setModifyStrPosition(delta.getTarget().getPosition());
            this.diffDetail.add(detailVO);
        }
    }

}
