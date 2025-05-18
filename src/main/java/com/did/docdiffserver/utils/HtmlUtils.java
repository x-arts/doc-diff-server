package com.did.docdiffserver.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.table.HtmlTableContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HtmlUtils {

    private static final Document.OutputSettings settings = new Document.OutputSettings()
            .prettyPrint(false)  // 禁用格式化
            .indentAmount(0);    // 无缩进


    /**
     * 添加属性并返回 文本
     * @param index
     * @return
     */
    public static String addTableId(String html, String tableId) {
        Document document = htmlToDocument(html);
        Element table = findFirstTable(document);
        table.attr("id", tableId);
        return document.outerHtml();
    }


    public static void adjustTableHead(HtmlTableContent htmlTableContent, Set<String> standHeads) {
        String tableHtml = htmlTableContent.getHtml();
        Document document = htmlToDocument(tableHtml);
        Element table = findFirstTable(document);
        String tableHeadLine = getTableHeadLine(table);

        if (standHeads.contains(tableHeadLine)) {
            // 表示是标准表头
            return;
        }

        // 表示第一行不是表头，需要一直往下找，找到表头
        Elements trs = table.select("tr");
        boolean isFindTableHead = false;
        List<Integer> removeIndex= new ArrayList<>();
        for (int i = 0; i < trs.size(); i++) {
            Element tr = trs.get(i);
            String headed = headTrToString(tr);
            if (standHeads.contains(headed)) {
                isFindTableHead = true;
                break;
            } else {
                removeIndex.add(i);
            }
        }

        if (isFindTableHead) {
            removeTableRowByIndex(table, removeIndex);

            // 修改了结构，需要重写内容
            htmlTableContent.setHtml(document.outerHtml());
        }
    }


    public static void removeTableRowByIndex(Element table,List<Integer> removeIndex)  {
        Elements tr = table.select("tr");
        for (int index : removeIndex) {
            tr.get(index).remove();
        }
    }




    /**
     * 获取表格的表头
     * @param tableHtmlList
     * @return
     */
    public static Set<String> getTableHeads(List<String> tableHtmlList) {
        Set<String> tableHeads = new HashSet<>();
        for (String tableHtml : tableHtmlList) {
            Document document = htmlToDocument(tableHtml);
            Element firstTable = findFirstTable(document);
            String tableHeadLine = getTableHeadLine(firstTable);
            tableHeads.add(tableHeadLine);
        }
        return tableHeads;
    }

    public static Element findFirstTable(Document document) {
        return document.select("table").first();
    }



    public static Document htmlToDocument(String html) {
        Document parse = Jsoup.parse(html);
        parse.outputSettings(settings);
        return parse;
    }


    /**
     * 获取表格的表头
     * @param table
     * @return
     */
    public static String getTableHeadLine(Element table) {
        Element header = table.select("tr").first();
//        Assert.notNull(header, "表格没有表头");
        return headTrToString(header);
    }

    private static String headTrToString(Element tr) {
        Elements tds = tr.select("td");
        List<String> headers = new ArrayList<>();
        for (Element td : tds) {
            headers.add(StrTools.removeSpaceInLine(td.text().trim()));
        }
        return StrUtil.join("|", headers);
    }


}
