package com.did.docdiffserver.data.vo;

import lombok.Data;

import java.util.List;

@Data
public class DiffResultItemVo {

    private List<DiffTextItemVO> diffItemList;


    private List<DiffTableItemVO> diffTableItemList;



    public static DiffResultItemVo of(List<DiffTextItemVO> diffItemList, List<DiffTableItemVO> tableItemList) {
        DiffResultItemVo diffResultItemVo = new DiffResultItemVo();
        diffResultItemVo.setDiffItemList(diffItemList);
        diffResultItemVo.diffTableItemList = tableItemList;
        diffResultItemVo.buildDetail();
        return diffResultItemVo;
    }


    public void buildDetail(){
        for (DiffTextItemVO diffTextItemVO : this.diffItemList) {
            diffTextItemVO.buildDiffDetail();
        }

        for (DiffTableItemVO diffTableItemVO : diffTableItemList) {
            diffTableItemVO.buildDiffDetail();
        }
    }
}
