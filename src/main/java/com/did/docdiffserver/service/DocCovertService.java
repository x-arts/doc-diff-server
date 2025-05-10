package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

/**
 * 文档转换服务
 */
@Slf4j
@Service
public class DocCovertService {


    @Resource
    private  MinerUService minerUService;

    @Resource
    private SofficeServcie sofficeServcie;


    public String doc2mdMinerU(String filePath, String fileId) {
        log.info("doc2mdMinerU: fileId={}", fileId);
        return   minerUService.docToMarkdown(filePath,fileId);
    }

    public String docx2Html(String filePath, String fileId){
        return sofficeServcie.doc2Html(filePath, fileId);
    }


    /**
     * 把 html 转换成纯文本，不包含表格
     *
     *
     * @param filePath
     * @return
     */
    public String html2PlanTextWithoutTable(String filePath) {
        String html = FileUtil.readString(filePath, "utf-8");
        Document doc = Jsoup.parse(html);
        for (Element table : doc.select("table")) {
            table.remove();
        }
        return doc.html();

    }

}
