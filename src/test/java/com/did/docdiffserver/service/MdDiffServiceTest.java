package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import com.did.docdiffserver.TestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;

@Slf4j
public class MdDiffServiceTest extends TestBase {


    @Resource
    private PdfFindLineDiffService revisedPdfFindDiffService;


    @Test
    public void simplePdfMdFindDiffTest() {
        String pdfSimpleMdFilePath = localTempFilePath + "pdf-1-simple.md";
        String dict = FileUtil.readString(localTempFilePath + "1/dict.txt", "utf-8");
        revisedPdfFindDiffService.simplePdfMdFindDiff(dict, pdfSimpleMdFilePath);
    }


}
