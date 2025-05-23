package com.did.docdiffserver.data.vo.table;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.utils.HtmlUtils;
import com.did.docdiffserver.utils.StrTools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {


    private String tableName;

    private String tableId;

    private List<Row> rows;

    /**
     * 表格查询的字段
     */
    private String tabDict;


    /**
     * 行的下标记录， 后续用来做二分查找
     */
    private ArrayList<Integer> compareDictIndex = new ArrayList<>();



    /**
     * 把 html 的表格转换成 tableInfo
     * @param htmlList
     * @return
     */
    public static List<TableInfo> buildTableInfoList(List<String> htmlList){
        return htmlList.stream().map(TableInfo::getTableInfo)
                .collect(Collectors.toList());
    }

    public static TableInfo getTableInfo(String html){
        if (StrUtil.isBlank(html)) {
            throw new RuntimeException("html is null");
        }
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        return TableInfo.of(table);
    }

    public static TableInfo of(Element table) {
        String tableHeadLine = HtmlUtils.getTableHeadLine(table);
        List<Row> rows = getRows(table);
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableHeadLine);
        tableInfo.setRows(rows);
        tableInfo.setTableId(table.attr("id"));
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
     * 构建比较字典
     */
    public void  buildTableDict() {
        List<String> rowLines = getRowLines();
        StringBuilder dict = new StringBuilder();
        for (int i = 0; i < rowLines.size(); i++) {
            String rowLine = rowLines.get(i);
            dict.append(rowLine);

            for (char c : rowLine.toCharArray()) {
                this.compareDictIndex.add(i);
            }
        }
        this.tabDict = dict.toString();
    }


    public List<String> getRowLines() {
        List<String> rowLines = new ArrayList<>();
        for (Row row : this.rows) {
            String compareRowLine = row.compareRowLine();
            rowLines.add(compareRowLine);
        }
        return rowLines;
    }


    public List<Integer> fetchMatchIndex(String matchText) {
        int startIndex = tabDict.indexOf(matchText);
        int endIndex = startIndex + matchText.length() ;

        Set<Integer> indexSet = new HashSet<>();

        for (int i = startIndex; i < endIndex; i++) {
            indexSet.add(compareDictIndex.get(i));
        }
        return new ArrayList<>(indexSet);
    }

    public static void main(String[] args) {
        String s = "0123456789";

        System.out.println(s.indexOf("3456"));
    }



}
