package com.did.docdiffserver.service.table;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.condition.TableCompareCondition;
import com.did.docdiffserver.data.vo.DiffTableItemVO;
import com.did.docdiffserver.data.vo.table.Row;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.utils.MergeTableUtils;
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
        MergeTableUtils.mergeTableInfoRow(pdfTable);

        List<Row> wordTableRows = word.getRows();
        List<Row> pdfTableRows = pdfTable.getRows();

        /**  后续优化的， 谁的表格大，谁在 for 循环 驱动对比， 小的表格会遗漏
         *
         * 如果一定要word 来循环，需要考虑 pdf 的表格 比 word 大
         */

        Map<Integer, String> pdfRowMap = new HashMap<>();

        for (int i = 0; i < pdfTableRows.size(); i++) {
            pdfRowMap.put(i, pdfTableRows.get(i).simpleRowLine());
        }

        List<DiffTableItemVO> diffTableItemList = new ArrayList<>();
        // pdf 的表格不一定有足够满足的下标
        for (int i = 0; i < wordTableRows.size(); i++) {
            String wordRowLine = wordTableRows.get(i).simpleRowLine();
            String pdfRowLine = pdfRowMap.get(i);
            if (!StrUtil.equals(wordRowLine, pdfRowLine)) {
                // not match  add  to resutl
                DiffTableItemVO diffItem = DiffTableItemVO.create(wordRowLine, pdfRowLine, word.getTableId(), i);
                diffTableItemList.add(diffItem);
            }
        }

        return diffTableItemList;
    }

}
