package com.did.docdiffserver.data.vo.table;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 表格需要调整的输出
 */
@Data
public class TableMergeResult {


    /**
     * 需要修改的行内容
     */
    private List<HtmlTableContent> reSetTables = new ArrayList<>();

    /**
     * 需要移除的行
     */
    private List<Integer> removeLineIndex = new ArrayList<>();



    public void add(TableMergeResult tableMergeResult) {
        this.reSetTables.addAll(tableMergeResult.getReSetTables());
        this.removeLineIndex.addAll(tableMergeResult.getRemoveLineIndex());
    }

    public static  TableMergeResult  createEmptyResult() {
        TableMergeResult result = new TableMergeResult();
        result.setReSetTables(Collections.emptyList());
        result.setRemoveLineIndex(Collections.emptyList());
        return result;
    }


    public static TableMergeResult create(HtmlTableContent tableContent, List<Integer> removeLineIndex){
        TableMergeResult result = new TableMergeResult();
        result.setReSetTables(Lists.newArrayList(tableContent));
        result.setRemoveLineIndex(removeLineIndex);
        return result;
    }




}
