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
public class DiffTextItemVO implements DiffItem{

    private String tableId;

    private Integer flagIndex;

    private String originalText;

    private String modifiedText;

    private List<Integer>  originalLineNumbers;

    /**
     * pdf 行索引
     */
    private List<Integer> modifyLineNumbers;

    private List<DiffItemDetailVO> diffDetail;


    public static DiffTextItemVO create(String originalText, String modifiedText, List<Integer> originalLineNumbers, List<Integer> modifyLineNumbers) {
        DiffTextItemVO diffTextItemVO = new DiffTextItemVO();
        diffTextItemVO.originalText = originalText;
        diffTextItemVO.modifiedText = modifiedText;
        diffTextItemVO.originalLineNumbers = originalLineNumbers;
        diffTextItemVO.modifyLineNumbers = modifyLineNumbers;
        return diffTextItemVO;
    }


    public void buildDiffDetail(){
        this.diffDetail = new ArrayList<>();
        if (StrUtil.isBlank(modifiedText)) {
            this.diffDetail.add(DiffItemDetailVO.createForFind(this.originalText));
            return;
        }
        List<String> originalChars = Arrays.asList(originalText.split(""));
        List<String> revisedChars = Arrays.asList(modifiedText.split(""));

        Patch<String> patch = DiffUtils.diff(originalChars, revisedChars);

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            DiffItemDetailVO detailVO = DiffItemDetailVO.of(delta);
            this.diffDetail.add(detailVO);
        }
    }

}
