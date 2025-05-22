package com.did.docdiffserver.utils;

import com.did.docdiffserver.data.vo.table.HtmlTableContent;
import com.did.docdiffserver.data.vo.table.TableMergeResult;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表格合并工具类
 */
public class MergeTableUtils {


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

//        int index = 1;

//        // 添加 id 属性
//        for (HtmlTableContent reSetTable : mergeResult.getReSetTables()) {
//            String html = HtmlUtils.addTableId(reSetTable.getHtml(), "table" + index);
//            reSetTable.setHtml(html);
//            index++;
//        }

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
