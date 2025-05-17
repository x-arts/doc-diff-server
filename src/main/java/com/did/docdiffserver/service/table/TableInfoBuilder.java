package com.did.docdiffserver.service.table;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.table.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TableInfoBuilder {


    /**
     * 把表格合并
     * @param tableOne
     * @param tableTwo
     * @return
     */
    public Element mergeTable(Element tableOne, Element tableTwo) {
        log.info("mergeTable");
        Elements tr = tableTwo.select("tr");
        tr.remove(0);
        for (Element element : tr) {
            tableOne.appendChild(element);
        }
        return tableOne;
    }


    /**
     * 把 html 的表格转换成 tableInfo
     * @param htmlList
     * @return
     */
    public List<TableInfo> buildTableInfoList(List<String> htmlList){
        return htmlList.stream().map(this::getTableInfo)
                .collect(Collectors.toList());
    }


    public TableInfo getTableInfo(String html){
        if (StrUtil.isBlank(html)) {
            throw new RuntimeException("html is null");
        }
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        return TableInfo.of(table);
    }


}
