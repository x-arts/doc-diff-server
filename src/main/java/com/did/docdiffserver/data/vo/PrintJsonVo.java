package com.did.docdiffserver.data.vo;

import lombok.Data;

import java.util.List;

@Data
public class PrintJsonVo {

    private List<DiffTextItemVO> diffItemList;



    public static PrintJsonVo of(List<DiffTextItemVO> diffItemList) {
        PrintJsonVo printJsonVo = new PrintJsonVo();
        printJsonVo.setDiffItemList(diffItemList);
        printJsonVo.buildDetail();
        return printJsonVo;
    }


    public void buildDetail(){
        for (DiffTextItemVO diffTextItemVO : this.diffItemList) {
            diffTextItemVO.buildDiffDetail();
        }
    }
}
