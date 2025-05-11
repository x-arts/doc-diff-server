package com.did.docdiffserver.service.compent;

import cn.hutool.core.io.FileUtil;
import com.did.docdiffserver.config.YamlConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

    @Resource
    private YamlConfig yamlConfig;

    @Resource
    private  StoreService  storeService;


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
    public String docx2Markdown(String filePath, String fileId) {
        String outDir = storeService.getProcessMarkDownDir(fileId);

        String scriptBasePath = yamlConfig.getScriptBasePath();
        String scriptPath = scriptBasePath + "/docx2md.sh";

        try {
            ProcessBuilder pb = new ProcessBuilder(scriptPath, filePath, outDir);
            pb.redirectErrorStream(true);  // 合并错误输出
            Process process = pb.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("MinerU doc2Markdown line = {}", line);
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return storeService.getProcessMarkDownFilePath(fileId);
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
