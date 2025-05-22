package com.did.docdiffserver.data.vo;

import lombok.Data;

import java.util.List;

@Data
public class PrintJsonVo {

    private List<DiffTextItemVO> diffItemList;


    private List<DiffTableItemVO> diffTableItemList;



    public static PrintJsonVo of(List<DiffTextItemVO> diffItemList, List<DiffTableItemVO> tableItemList) {
        PrintJsonVo printJsonVo = new PrintJsonVo();
        printJsonVo.setDiffItemList(diffItemList);
        printJsonVo.diffTableItemList = tableItemList;
        printJsonVo.buildDetail();
        return printJsonVo;
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
