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

    private String originalStr;

    private int originalStrPosition;

    private List<String> modifyStr;

    private int modifyStrPosition;

    public static  DiffItemDetailVO createForFind(String originalStr){
        DiffItemDetailVO detailVO = new DiffItemDetailVO();
        detailVO.type = DeltaType.DELETE.toString();
        detailVO.originalStr = originalStr;
        detailVO.originalStrPosition = 0;
        detailVO.modifyStr = new ArrayList<>();
        detailVO.modifyStrPosition = 0;
        return detailVO;

    }

    public static  DiffItemDetailVO of(AbstractDelta<String> delta) {
        DiffItemDetailVO detailVO = new DiffItemDetailVO();
        detailVO.setType(delta.getType().toString());
        List<String> sourceLine = delta.getSource().getLines();
        if (CollectionUtil.isNotEmpty(sourceLine)) {
            detailVO.originalStr = sourceLine.get(0);
        }
        detailVO.originalStrPosition = delta.getSource().getPosition();

        detailVO.modifyStr = delta.getTarget().getLines();
        detailVO.modifyStrPosition = delta.getTarget().getPosition();
        return detailVO;
    }
}
