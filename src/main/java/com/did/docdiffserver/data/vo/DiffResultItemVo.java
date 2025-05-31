package com.did.docdiffserver.data.vo;

import com.did.docdiffserver.data.vo.table.DiffTableFlag;
import com.did.docdiffserver.data.vo.table.DiffTableItemVO;
import lombok.Data;

import java.util.List;

@Data
public class DiffResultItemVo {

    private List<DiffTextItemVO> diffItemList;

    private List<DiffTableFlag> diffTableItemList;



    public static DiffResultItemVo of(List<DiffTextItemVO> diffItemList, List<DiffTableFlag> tableItemList) {
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

        for (DiffTableFlag diffTableItemVO : diffTableItemList) {
            diffTableItemVO.buildDiffDetail();
        }
    }
}
