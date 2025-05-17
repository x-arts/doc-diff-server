package com.did.docdiffserver.data.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {

    private String headersLine;

    private int columnSize;

    private String tableName;

    private List<Row> rows;


    public static TableInfo init(String headersLine, int columnSize) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(headersLine);
        tableInfo.setColumnSize(columnSize);
        tableInfo.setHeadersLine(headersLine);
        tableInfo.setRows(new ArrayList<>());
        return tableInfo;
    }

    public void addRows(List<Row> rows) {
        this.rows.addAll(rows);
    }

}
