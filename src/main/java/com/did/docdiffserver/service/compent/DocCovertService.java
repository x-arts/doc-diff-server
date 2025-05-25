package com.did.docdiffserver.service.compent;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.did.docdiffserver.compent.exception.BusinessException;
import com.did.docdiffserver.compent.exception.ErrorCode;
import com.did.docdiffserver.config.StoreConfig;
import com.did.docdiffserver.config.YamlConfig;
import com.did.docdiffserver.data.entity.ContractDiffTask;
import com.did.docdiffserver.data.entity.ContractDiffTaskDetail;
import com.did.docdiffserver.data.entity.FileStore;
import com.did.docdiffserver.repository.ContractDiffTaskDetailRepository;
import com.did.docdiffserver.repository.FileStoreRepository;
import com.did.docdiffserver.service.PdfService;
import com.did.docdiffserver.service.WordService;
import com.did.docdiffserver.utils.AssertUtil;
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
    private StoreConfig storeConfig;

    @Resource
    private WordService wordService;

    @Resource
    private PdfService pdfService;

    @Resource
    private FileStoreRepository fileStoreRepository;

    @Resource
    private ContractDiffTaskDetailRepository taskDetailRepository;


    /**
     *  文本转换成 markdown
     * @param diffTask
     */
    public ContractDiffTaskDetail  docConventMarkdown(ContractDiffTask diffTask) {
        String standardFileId = diffTask.getStandardFileId();
        FileStore standFileStore = fileStoreRepository.findByFileId(standardFileId);
        String strandFilePath = standFileStore.getFilePath();

        // word 文档的转换
        String markdownFilePath = docx2Markdown(strandFilePath, standardFileId);
//        String wordMdFormatContent = wordService.formatShowMarkdown(markdownFilePath);

        // 存入数据库
        String wordMdFileId = UUID.randomUUID().toString();
        FileStore markdownFileStore = FileStore.createLocalFile(wordMdFileId, markdownFilePath,"MD" );
        fileStoreRepository.save(markdownFileStore);


        // pdf 文档转换
        String compareFileId = diffTask.getCompareFileId();
        FileStore compareFileStore = fileStoreRepository.findByFileId(compareFileId);
        String compareFilePath = compareFileStore.getFilePath();

        String pdfMdFileId = UUID.randomUUID().toString();
        String pdfMarkdownFilePath = doc2mdMinerU(compareFilePath, compareFileId);
        FileStore pdfMarkdownFileStore = FileStore.createLocalFile(pdfMdFileId, pdfMarkdownFilePath,"MD" );
        fileStoreRepository.save(pdfMarkdownFileStore);

        ContractDiffTaskDetail diffTaskDetail = ContractDiffTaskDetail.createForAdd(diffTask.getId(), wordMdFileId, pdfMdFileId);

        taskDetailRepository.save(diffTaskDetail);

        return diffTaskDetail;

    }




    public String doc2md(String fileId) {
        FileStore fileStore = fileStoreRepository.findByFileId(fileId);
        AssertUtil.notNull(fileStore, "File not found for fileId: " + fileId);
        String suffix = fileStore.getFormat();
        String filePath = fileStore.getFilePath();

        if (suffix.equals("docx")) {
            String markdownFilePath = docx2Markdown(filePath, fileId);
            return wordService.formatShowMarkdown(markdownFilePath);
        }

        if (suffix.equals("pdf")) {
            return doc2mdMinerU(filePath, fileId);
        }

        throw new BusinessException(ErrorCode.ILLEGAL_NOT_ASSERT.code,  "文件格式不支持: " + suffix);
    }


    public String doc2mdMinerU(String filePath, String fileId) {
        log.info("doc2mdMinerU: fileId={}", fileId);
        return  minerUService.docToMarkdown(filePath,fileId);
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
        String outDir = storeConfig.getProcessMarkDownDir(fileId);
        String mdFilePath = outDir + fileId + ".md";

        FileUtil.touch(mdFilePath);

        String scriptBasePath = yamlConfig.getScriptBasePath();
        String scriptPath = scriptBasePath + "docx2md.sh";

        try {
            ProcessBuilder pb = new ProcessBuilder(scriptPath, filePath, mdFilePath);
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

        return storeConfig.getWordMarkDownFilePath(fileId);
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
