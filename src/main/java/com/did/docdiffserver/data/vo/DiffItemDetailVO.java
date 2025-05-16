package com.did.docdiffserver.data.vo;

import lombok.Data;

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

    private String modifyStr;

    private int modifyStrPosition;


    public static DiffItemDetailVO createDeleteAll(String type, String originalStr,  String modifyStr) {
        DiffItemDetailVO diffItemDetailVO = new DiffItemDetailVO();
        diffItemDetailVO.setType("type");
        diffItemDetailVO.setOriginalStr(originalStr);
        diffItemDetailVO.setModifyStr(modifyStr);
        return null;
    }
}
