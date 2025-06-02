package com.did.docdiffserver.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.did.docdiffserver.data.vo.table.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表格合并工具类
 */
public class MergeTableUtils {


    /**
     * 一个表格里面的行合并
     */
    public static void mergeTableInfoRow(TableInfo tableInfo){
        List<Row> rows = tableInfo.getRows();
        List<Integer> firstEmptyCellRowIndex = firstEmptyCellRowIndex(rows);
        if (CollectionUtil.isEmpty(firstEmptyCellRowIndex)) {
            return;
        }
        for (int emptyCellRowIndex : firstEmptyCellRowIndex) {
            mergeRow(rows.get(emptyCellRowIndex-1), rows.get(emptyCellRowIndex));
        }

        // remove Row
        int deleteCount  = 0;
        for (int emptyCellRowIndex : firstEmptyCellRowIndex) {
            rows.remove(emptyCellRowIndex - deleteCount);
            deleteCount++;
        }

    }

    private static void mergeRow(Row row, Row branchRow) {
        List<Cell> branchCells = branchRow.getCells();
        Map<Integer, Cell> branchCellsMap = new HashMap<>();
        for (int i = 0; i < branchCells.size(); i++) {
            Cell cell = branchCells.get(i);
            branchCellsMap.put(i, cell);
        }

        List<Cell> masterCells = row.getCells();
        for (int i = 0; i < masterCells.size(); i++) {
            Cell cell = masterCells.get(i);
            Cell branchCell = branchCellsMap.get(i);
            if (branchCell != null) {
                cell.setText(cell.getText() + branchCell.getText());
            }
        }
    }


    /**
     * 获取第一列是空的行数
     * @param rows
     * @return
     */
    private static  List<Integer> firstEmptyCellRowIndex(List<Row> rows) {
        List<Integer> hasEmptyCellRow = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            if (row.onlyLastOneCellNotEmpty()) {
                hasEmptyCellRow.add(i);
            }
        }

        return hasEmptyCellRow;
    }



    public static TableMergeResult mergeTable(List<HtmlTableContent> tableContents, Set<String> standHeads) {
        List<List<HtmlTableContent>> spiltGroups = new ArrayList<>();
        List<HtmlTableContent>  group = new ArrayList<>();


        TableMergeResult mergeResult = new TableMergeResult();

        for (HtmlTableContent tableContent : tableContents) {
            if (tableContent.isTable()) {
                group.add(tableContent);
            } else {
                // 要用一个新的对象
                spiltGroups.add(new ArrayList<>(group));
                group.clear();
            }
        }
        for (List<HtmlTableContent> spiltGroup : spiltGroups) {
            TableMergeResult result = mergeTableSameGroup(spiltGroup);
            mergeResult.add(result);
        }


        return mergeResult;
    }


    /**
     * 同一个组的表格合并
     * @param tableContents
     */
    private static TableMergeResult  mergeTableSameGroup(List<HtmlTableContent> tableContents) {
        /**
         * 我断言不存在，不同的表格是没有文字来分割的。  所以在同一个组的一定是同一个表格
         * 所以，采取 统一合并的方式
          */
        if (tableContents.size() == 1) {
            return TableMergeResult.createEmptyResult();
        }

        HtmlTableContent masterTable = tableContents.get(0);
        List<HtmlTableContent> branchTable = tableContents.subList(1, tableContents.size());

        List<Integer> removeIndex = branchTable.stream()
                .map(HtmlTableContent::getIndex)
                .collect(Collectors.toList());

        mergeTableList(masterTable, branchTable);

        return TableMergeResult.create(masterTable, removeIndex);
    }


    private static void mergeTableList(HtmlTableContent master, List<HtmlTableContent> branchTableList) {
        Document document = HtmlUtils.htmlToDocument(master.getHtml());
        Element masterTable = HtmlUtils.findFirstTable(document);
        for (HtmlTableContent branchTableContent : branchTableList) {
            Element branchTable = HtmlUtils.findFirstTable(HtmlUtils.htmlToDocument(branchTableContent.getHtml()));
            mergeTable(masterTable, branchTable);
        }

        master.setHtml(document.outerHtml());
    }

    private static void mergeTable(Element masterTable, Element branchTable) {
        Elements tr = branchTable.select("tr");
        tr.remove(0); // 移除表头
        for (Element element : tr) {
            masterTable.appendChild(element);
        }
    }




}
