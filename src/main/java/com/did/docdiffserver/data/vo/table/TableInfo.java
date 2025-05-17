package com.did.docdiffserver.data.vo.table;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.utils.StrTools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {


    private String tableName;

    private List<Row> rows;


    public static TableInfo of(Element table) {
        String tableHeadLine = getTableHeadLine(table);
        List<Row> rows = getRows(table);
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableHeadLine);
        tableInfo.setRows(rows);
        return tableInfo;
    }

    private static List<Row> getRows(Element table) {
        Elements trs = table.select("tr");
        List<Row> rows = new ArrayList<>();
        int count = 0;
        for (Element tr : trs) {
            count++;
            if (count == 1) {
                continue;
            }
            rows.add(Row.of(tr));
        }
        return rows;
    }




    /**
     * 获取表格的表头
     * @param table
     * @return
     */
    public static String getTableHeadLine(Element table) {
        Element header = table.select("tr").first();
        Elements tds = header.select("td");
        int columnSize = tds.size();
        List<String> headers = new ArrayList<>();
        for (Element td : tds) {
            headers.add(td.text());
        }
        return StrUtil.join("|", headers);
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
