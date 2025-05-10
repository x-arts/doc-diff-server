package com.did.docdiffserver.data.vo;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.utils.StrTools;
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


    public List<String> getRowLines() {
        List<String> rowLines = new ArrayList<>();
        for (Row row : this.rows) {
            List<Cell> cells = row.getCells();
            String rowLine = StrUtil.join("|", cellsToText(cells));
            rowLines.add(rowLine);
        }
        return rowLines;
    }


    public List<String> cellsToText(List<Cell> cells) {
        List<String> lines = new ArrayList<>();
        for (Cell cell : cells) {
            String cellText = StrTools.removeSpaceInLine(cell.getText());
            cellText = StrTools.replacePunctuation(cellText);
            lines.add(cellText);
        }
        return lines;
    }


}
