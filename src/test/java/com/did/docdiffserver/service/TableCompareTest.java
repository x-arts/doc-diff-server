package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.TestBase;
import com.did.docdiffserver.data.vo.table.DiffTableFlag;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.utils.MergeTableUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableCompareTest extends TestBase {


    @Resource
    private TableContentCompareService tableContentCompareService;


    @Test
    public void tableCompareTest() {
        String wordTablePath = localTempFilePath + "compare/table/word.md";
        String pdfTablePath = localTempFilePath + "compare/table/pdf.md";


        List<DiffTableFlag> diffTableFlags = new ArrayList<>();


        List<String> wordLines = FileUtil.readLines(wordTablePath, StandardCharsets.UTF_8);

        wordLines =  wordLines.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());

        List<String> pdfLines = FileUtil.readLines(pdfTablePath, StandardCharsets.UTF_8);

        pdfLines =  pdfLines.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());


        int startIndex = 4000;

        List<TableInfo> wordTableInfos =  TableInfo.buildTableInfoList(wordLines);
        List<TableInfo> pdfTableInfos =  TableInfo.buildTableInfoList(pdfLines);

        for (int i = 0; i < wordLines.size(); i++) {
            TableInfo wordTableInfo = wordTableInfos.get(i);
            TableInfo pdfTableInfo = pdfTableInfos.get(i);
            MergeTableUtils.mergeTableInfoRow(pdfTableInfo);

            List<DiffTableFlag> diffTableFlags1 = tableContentCompareService.compareTableContent(wordTableInfo, pdfTableInfo, startIndex + i);

            System.out.println(" i  = "  + i + " size = " + diffTableFlags1.size() );
            diffTableFlags.addAll(diffTableFlags1);
        }
        /**
         *
         *  0-1
         *  1-1
         *  2- 104
         *  3-19
         *  4-0
         *  5-13
         *  6-0
         *
         */

        for (TableInfo wordTableInfo : wordTableInfos) {
            System.out.println(wordTableInfo.toHtmlTable());
        }

        for (TableInfo pdfTable : pdfTableInfos) {
            System.out.println(pdfTable.toHtmlTable());
        }


    }


}
