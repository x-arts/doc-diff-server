package com.did.docdiffserver.service;

import com.did.docdiffserver.TestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;

@Slf4j
public class DocDiffServiceTest extends TestBase {


    @Resource
    private DocDiffService docDiffService;

    @Resource
    private WordService wordService;

    @Resource
    private PdfService pdfService;



    @Test
    public void docDiffTest() {
        String wordFileId = "a91b1188-cc50-462e-952d-ad685abf9660";
        String pdfFileId = "b2b4a8f7-42b1-49f7-adc6-68d159573100";
        String diffStr = docDiffService.docDiff(wordFileId, pdfFileId);
    }

    @Test
    public void wordFormatTest() {
        String path = "/Users/xuewenke/doc-diff-server/process/markdown/a91b1188-cc50-462e-952d-ad685abf9660/a91b1188-cc50-462e-952d-ad685abf9660.md";
        wordService.formatShowMarkdown(path);
    }

    @Test
    public void pdfFormatTest() {
        String path = "/Users/xuewenke/doc-diff-server/process/markdown/b2b4a8f7-42b1-49f7-adc6-68d159573100/auto/b2b4a8f7-42b1-49f7-adc6-68d159573100.md";
        pdfService.formatShowMarkdown(path);


    }


}
