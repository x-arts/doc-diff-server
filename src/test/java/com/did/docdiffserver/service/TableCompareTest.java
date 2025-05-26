package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSONObject;
import com.did.docdiffserver.TestBase;
import com.did.docdiffserver.data.vo.table.DiffTableFlag;
import com.did.docdiffserver.data.vo.table.TableInfo;
import org.junit.Test;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TableCompareTest extends TestBase {


    @Resource
    private TableContentCompareService tableContentCompareService;


    @Test
    public void tableCompareTest() {
        String wordTablePath = localTempFilePath + "compare/table/word.md";
        String pdfTablePath = localTempFilePath + "compare/table/pdf.md";


        List<String> wordLines = FileUtil.readLines(wordTablePath, StandardCharsets.UTF_8);
        List<String> pdfLines = FileUtil.readLines(pdfTablePath, StandardCharsets.UTF_8);

        TableInfo wordTableInfo = TableInfo.getTableInfo(wordLines.get(0));

        TableInfo pdfTableInfo = TableInfo.getTableInfo(pdfLines.get(0));

        List<DiffTableFlag> diffTableFlags = tableContentCompareService.compareTableContent(wordTableInfo, pdfTableInfo);

        System.out.println(diffTableFlags.size());

        System.out.println(JSONObject.toJSONString(diffTableFlags));
    }


}
