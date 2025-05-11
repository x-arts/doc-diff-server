package com.did.docdiffserver.service.compent;

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

    public String docx2HtmlAndGet(String filePath, String fileId) {
        File file = new File(filePath);
        String parent = file.getParent();
        String outDir = parent + "/" + "html/" + fileId;
        FileUtil.mkdir(outDir);
        boolean b = docx2Html(filePath, outDir);
        if (b) {
            return FileUtil.readString(outDir + "/" + fileId + ".html", "utf-8");
        } else {
            return "";
        }
    }

    public boolean docx2Html(String filePath, String outDir){
        return sofficeServcie.doc2Html(filePath, outDir);
    }


    /**
     *
     * @param filePath
     * @return 返回 markdown 的文件路径
     */
    public String docx2Markdown(String filePath) {
        // todo



        return "";
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
