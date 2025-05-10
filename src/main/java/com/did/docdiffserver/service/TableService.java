package com.did.docdiffserver.service;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.Row;
import com.did.docdiffserver.data.vo.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TableService {


    public TableInfo getTableInfo(String html){
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        return getTableInfo(table);
    }


    public TableInfo getTableInfo(Element table) {
        Element header = table.select("tr").first();
        Elements tds = header.select("td");
        int columnSize = tds.size();
        List<String> headers = new ArrayList<>();
        for (Element td : tds) {
            headers.add(td.text());
        }
        String headersLine = StrUtil.join("|", headers);
        TableInfo tableInfo = TableInfo.init(headersLine, columnSize);

        List<Row> rows = tableInfo.getRows();
        Elements trs = table.select("tr");
        int count = 0;
        for (Element tr : trs) {
            count++;
            if (count == 1) {
                continue;
            }
            rows.add(Row.of(tr));
        }
        tableInfo.addRows(rows);

        return tableInfo;
    }

}
