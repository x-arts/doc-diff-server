package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import com.did.docdiffserver.TestBase;
import com.did.docdiffserver.utils.HtmlMarkUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class MdDiffServiceTest {



    private static Set<String> keyWordSet = new HashSet<>();

    static {
        keyWordSet.add("周期");
        keyWordSet.add("价格");
        keyWordSet.add("设备");
    }


    @Test
    public void highlightKeywordsInHtmlTest() throws IOException {
        String targetPath = "/Users/xuewenke/temp-file/doc/pandoc/html/soffice/1.html";
        String html =  StreamUtils.copyToString(Files.newInputStream(Paths.get(targetPath)), StandardCharsets.UTF_8);
        String markHtml = HtmlMarkUtil.highlightKeywords(html, keyWordSet);

        FileUtil.writeString(markHtml,new File("/Users/xuewenke/temp-file/doc/pandoc/html/soffice/markShow.html"),"utf-8");
        System.out.println("完成");
    }


    @Test
    public void highlightKeywordsInHtmlServiceTest() throws IOException {
        String targetPath = "/Users/xuewenke/temp-file/doc/pandoc/html/soffice/1.html";
        String html =  StreamUtils.copyToString(Files.newInputStream(Paths.get(targetPath)), StandardCharsets.UTF_8);

        MdDiffService service = new MdDiffService();
        String markHtml = service.highlightKeywordsInHtml(html);

        FileUtil.writeString(markHtml,new File("/Users/xuewenke/temp-file/doc/pandoc/html/soffice/markShow.html"),"utf-8");
        System.out.println("完成");
    }



 }
