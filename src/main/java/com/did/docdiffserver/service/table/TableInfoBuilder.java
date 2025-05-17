package com.did.docdiffserver.service.table;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.table.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TableInfoBuilder {



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
