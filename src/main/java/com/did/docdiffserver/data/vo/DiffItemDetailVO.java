package com.did.docdiffserver.data.vo;

import cn.hutool.core.collection.CollectionUtil;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DiffItemDetailVO {

    /**
     * change
     * delete
     * insert
     */
    private String type;

    private List<String> originalDiffChars;

    private int originalStrPosition;

    private List<String> modifyDiffChars;

    private int modifyStrPosition;

    public static  DiffItemDetailVO createForFind(String originalStr){
        DiffItemDetailVO detailVO = new DiffItemDetailVO();
        detailVO.type = DeltaType.DELETE.toString();
        detailVO.originalDiffChars = CollectionUtil.newArrayList(originalStr);
        detailVO.originalStrPosition = 0;
        detailVO.modifyDiffChars = new ArrayList<>();
        detailVO.modifyStrPosition = 0;
        return detailVO;

    }

    public static  DiffItemDetailVO of(AbstractDelta<String> delta) {
        DiffItemDetailVO detailVO = new DiffItemDetailVO();
        detailVO.setType(delta.getType().toString());
        detailVO.originalDiffChars = delta.getSource().getLines();
        detailVO.originalStrPosition = delta.getSource().getPosition();

        detailVO.modifyDiffChars = delta.getTarget().getLines();
        detailVO.modifyStrPosition = delta.getTarget().getPosition();
        return detailVO;
    }
}
