package com.did.docdiffserver.data.vo.table;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.DiffItemDetailVO;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class DiffTableFlag {

    private String originalText;

    private String modifiedText;

    private Integer flagIndex;

    private List<DiffItemDetailVO> diffDetail;


    public static DiffTableFlag create(String originalText, String modifiedText, int flagIndex) {
        DiffTableFlag item = new DiffTableFlag();
        item.originalText = originalText;
        item.modifiedText = modifiedText;
        item.flagIndex = flagIndex;

        item.buildDiffDetail();

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
