package com.did.docdiffserver.service.table;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.condition.TableCompareCondition;
import com.did.docdiffserver.data.vo.DiffTableItemVO;
import com.did.docdiffserver.data.vo.table.Row;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.utils.MergeTableUtils;
import com.did.docdiffserver.utils.SearchUtils;
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

        // 表格里的行数合并
//        MergeTableUtils.mergeTableInfoRow(pdfTable);
        pdfTable.buildTableDict();

        String tabDict = pdfTable.getTabDict();

        List<Row> wordTableRows = word.getRows();


        List<DiffTableItemVO> diffTableItemList = new ArrayList<>();
        // pdf 的表格不一定有足够满足的下标
        for (int i = 0; i < wordTableRows.size(); i++) {
            String wordRowLine = wordTableRows.get(i).compareRowLine();
            String pdfRowLine = SearchUtils.findMatchText(tabDict, wordRowLine);

            if (!StrUtil.equals(wordRowLine, pdfRowLine)) {
                // not match  add  to resutl
                DiffTableItemVO diffItem = DiffTableItemVO.create(wordRowLine, pdfRowLine, word.getTableId(), i, pdfTable.fetchMatchIndex(pdfRowLine));
                diffTableItemList.add(diffItem);
            }
        }

        return diffTableItemList;
    }

}
