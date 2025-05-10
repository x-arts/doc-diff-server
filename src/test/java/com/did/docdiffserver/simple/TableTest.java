package com.did.docdiffserver.simple;

import cn.hutool.core.io.FileUtil;
import com.did.docdiffserver.TestBase;
import com.did.docdiffserver.data.vo.TableInfo;
import com.did.docdiffserver.service.RevisedPdfFindDiffService;
import com.did.docdiffserver.service.TableService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

public class TableTest extends TestBase {

    @Resource
    private TableService tableService;

    @Resource
    private RevisedPdfFindDiffService revisedPdfFindDiffService;



    @Test
    public void  tableCompare() {
        String pdfSimpleMdFilePath = localTempFilePath + "pdf-1.md";
        String dict = FileUtil.readString(localTempFilePath + "table/table-dict.txt", "utf-8");
        revisedPdfFindDiffService.pdfTableFindDiff(dict, pdfSimpleMdFilePath);
    }


    @Test
    public void createTableDict() {
//        String dictFilePath = localTempFilePath + "1/dict.txt";
        String html = FileUtil.readString(localTempFilePath + "1/1.html", "utf-8");
        Document doc = Jsoup.parse(html);
        Elements tables = doc.select("table");
        List<TableInfo> tableInfos = new LinkedList<>();
        for (Element table : tables) {
            TableInfo tableInfo = tableService.getTableInfo(table);
            tableInfos.add(tableInfo);
        }

        List<String> allTableLines = new LinkedList<>();

        for (TableInfo tableInfo : tableInfos) {
            List<String> tableLines = new LinkedList<>();
            String tableName = tableInfo.getTableName();
            List<String> rowLines = tableInfo.getRowLines();
            tableLines.add(tableName);
            tableLines.addAll(rowLines);

            allTableLines.addAll(tableLines);

//            allTableLines.add("---------------------------");
        }

        TableInfo tableInfo = tableInfos.get(0);
        List<String> rowLines = tableInfo.getRowLines();
        rowLines.add(0, tableInfo.getTableName());

        FileUtil.writeLines(allTableLines, localTempFilePath + "table/table-text.txt", "utf-8");

        FileUtil.writeString(String.join("", allTableLines), localTempFilePath + "table/table-dict.txt", "utf-8");


    }
}
