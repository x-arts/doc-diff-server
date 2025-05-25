package com.did.docdiffserver.data.condition;

import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import lombok.Data;

import java.util.List;

/**
 * @author xuewenke
 * @since 2025/5/22 00:13
 */
@Data
public class TableCompareCondition {


    private List<TableInfo> wordTableInfoList;

    private List<TableInfo> pdftableInfoList;


    public static TableCompareCondition of(WordProcessVO word, PdfProcessVO pdf) {
        TableCompareCondition condition = new TableCompareCondition();
        condition.setWordTableInfoList(word.getTableInfoList());
        condition.setPdftableInfoList(pdf.getTableInfoList());
        return condition;
    }


}