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
public class DiffTableItemVO {

    private String originalText;

    private String modifiedText;

    private String tableId;

    /**
     *  行索引
     */
    private int wordTableRowIndex;

    /**
     * pdf 表格的行索引
     */
    private List<Integer> pdfTableRowIndex;



    private List<DiffItemDetailVO> diffDetail;


    public static DiffTableItemVO create(String originalText, String modifiedText, String tableId, int rowIndex, List<Integer> pdfLineIndex) {
        DiffTableItemVO item = new DiffTableItemVO();
        item.originalText = originalText;
        item.modifiedText = modifiedText;
        item.tableId = tableId;
        item.wordTableRowIndex = rowIndex;
        item.pdfTableRowIndex = pdfLineIndex;
        return item;
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
