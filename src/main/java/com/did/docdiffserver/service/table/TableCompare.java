package com.did.docdiffserver.service.table;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.condition.TableCompareCondition;
import com.did.docdiffserver.data.vo.DiffTableItemVO;
import com.did.docdiffserver.data.vo.table.Row;
import com.did.docdiffserver.data.vo.table.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TableCompare {

    /**
     *
     * @param condition
     */
    public List<DiffTableItemVO> tableInfoCompare(TableCompareCondition condition){
        List<TableInfo> wordTableInfoList = condition.getWordTableInfoList();
        List<TableInfo> pdftableInfoList = condition.getPdftableInfoList();

        List<DiffTableItemVO> diffList = new ArrayList<>();

        Map<String, TableInfo> pdfTableInfoMap = pdftableInfoList
                .stream()
                .collect(Collectors.toMap(TableInfo::getTableId, tableInfo -> tableInfo));

        for (TableInfo tableInfo : wordTableInfoList) {
            TableInfo pdfTableInfo = pdfTableInfoMap.get(tableInfo.getTableId());
            List<DiffTableItemVO> diffTableItemVOS = doTableInfoCompare(tableInfo, pdfTableInfo);
            diffList.addAll(diffTableItemVOS);
        }

        return diffList;
    }


    /**
     * 执行单个表格的对比
     * @param word
     * @param pdfTable
     */
    public List<DiffTableItemVO> doTableInfoCompare(TableInfo word, TableInfo pdfTable) {
        List<Row> wordTableRows = word.getRows();
        List<Row> pdfTableRows = pdfTable.getRows();


        List<DiffTableItemVO> diffTableItemList = new ArrayList<>();

        for (int i = 0; i < wordTableRows.size(); i++) {
            Row row = wordTableRows.get(i);
            String wordRowLine = row.simpleRowLine();
            String pdfRowLine = pdfTableRows.get(i).simpleRowLine();

            if (!StrUtil.equals(wordRowLine, pdfRowLine)) {
                // not match  add  to resutl
                DiffTableItemVO diffItem = DiffTableItemVO.create(wordRowLine, pdfRowLine, word.getTableId(), i);
                diffTableItemList.add(diffItem);
            }
        }

        return diffTableItemList;
    }

}
