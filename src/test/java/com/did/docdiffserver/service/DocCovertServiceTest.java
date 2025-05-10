package com.did.docdiffserver.service;


import cn.hutool.core.io.FileUtil;
import com.did.docdiffserver.TestBase;
import org.junit.Test;

import javax.annotation.Resource;

public class DocCovertServiceTest extends TestBase {

        private static final String localTempFilePath = "/Users/xuewenke/code/DID/web-server/doc-diff-server/src/main/temp-file/";
//    private static final String localTempFilePath =  "/Users/xuewenke/workspace/code/doc-diff-server/src/main/temp-file/";


    @Resource
    private DocCovertService covertService;


    @Test
    public void html2PlanTextWithoutTable() {
        String noTableHtml = covertService.html2PlanTextWithoutTable(localTempFilePath + "1/1.html");
        FileUtil.writeString(noTableHtml, localTempFilePath + "1/1-notable.html", "utf-8");

    }
}
