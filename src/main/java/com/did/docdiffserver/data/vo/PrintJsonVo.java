package com.did.docdiffserver.data.vo;

import lombok.Builder;
import lombok.Data;
import sun.security.krb5.internal.rcache.DflCache;

import java.util.List;

@Data
public class PrintJsonVo {

    private List<DiffItemVO> diffItemList;



    public static PrintJsonVo of(List<DiffItemVO> diffItemList) {
        PrintJsonVo printJsonVo = new PrintJsonVo();
        printJsonVo.setDiffItemList(diffItemList);
        printJsonVo.buildDetail();
        return printJsonVo;
    }


    public void buildDetail(){
        for (DiffItemVO diffItemVO : this.diffItemList) {
            diffItemVO.buildDiffDetail();
        }
    }
}
