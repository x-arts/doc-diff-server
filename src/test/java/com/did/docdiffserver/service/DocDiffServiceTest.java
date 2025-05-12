package com.did.docdiffserver.service;

import com.did.docdiffserver.TestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;

@Slf4j
public class DocDiffServiceTest extends TestBase {


    @Resource
    private DocDiffService docDiffService;


    @Test
    public void docDiffTest() {
        String wordFileId = "word-1";
        String pdfFileId = "";
        docDiffService.docDiff(wordFileId, pdfFileId);
    }



}
