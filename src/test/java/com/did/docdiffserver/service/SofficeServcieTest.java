package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import com.did.docdiffserver.TestBase;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;

public class SofficeServcieTest extends TestBase {

    @Resource
    private SofficeServcie sofficeServcie;

    @Test
    public void doc2Html(){
        String filePath = "/Users/xuewenke/temp-file/doc/soffice/1.docx";
        String fileId = "1";
        File file = new File(filePath);
        String parent = file.getParent();
        String outDir = parent + "/" + "html/" + fileId;
        FileUtil.mkdir(outDir);
        sofficeServcie.doc2Html(filePath, outDir);
    }




}
