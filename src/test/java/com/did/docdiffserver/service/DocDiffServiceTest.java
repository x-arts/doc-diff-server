package com.did.docdiffserver.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.did.docdiffserver.TestBase;
import com.did.docdiffserver.data.entity.ContractDiffTaskDetail;
import com.did.docdiffserver.data.entity.FileStore;
import com.did.docdiffserver.data.vo.DiffResultItemVo;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.config.StoreConfig;
import com.did.docdiffserver.data.vo.task.TaskCompareResultVO;
import com.did.docdiffserver.repository.ContractDiffTaskDetailRepository;
import com.did.docdiffserver.repository.FileStoreRepository;
import com.did.docdiffserver.utils.HtmlUtils;
import com.did.docdiffserver.utils.MergeTableUtils;
import com.did.docdiffserver.utils.StrTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DocDiffServiceTest extends TestBase {


    @Resource
    private DocDiffService docDiffService;

    @Resource
    private WordService wordService;

    @Resource
    private PdfService pdfService;

    @Resource
    private StoreConfig storeConfig;

    @Resource
    private FileStoreRepository fileStoreRepository;

    @Resource
    private ContractDiffTaskDetailRepository diffTaskDetailRepository;


    @Test
    public void pdfProcessVoTest(){
        String pdfFileId = "b2b4a8f7-42b1-49f7-adc6-68d159573100";
        PdfProcessVO pdfProcess = pdfService.process(storeConfig.getPdfMarkDownFilePath(pdfFileId), pdfFileId);
        List<TableInfo> tableInfoList = pdfProcess.getTableInfoList();

        TableInfo tableInfo = tableInfoList.get(2);

        tableInfo.getRows().forEach(row -> {
            System.out.println(row.simpleRowLine());
        });

        MergeTableUtils.mergeTableInfoRow(tableInfo);

        tableInfo.getRows().forEach(row -> {
            System.out.println(row.simpleRowLine());
        });

    }


    @Test
    public void docDiffTest() {
        String wordFileId = "a91b1188-cc50-462e-952d-ad685abf9660";
        String pdfFileId = "b2b4a8f7-42b1-49f7-adc6-68d159573100";
        TaskCompareResultVO result = docDiffService.docDiff(wordFileId, pdfFileId);

        String baseDir = storeConfig.getShowMarkdownBasePath();
        List<String> wordMarkDownList = result.getWordProcess().getMarkDownList();
        String fileId =  UUID.randomUUID().toString();
        String wordMdFilePath = baseDir + fileId +".md";
        FileStore localFile = FileStore.createLocalFile(fileId, wordMdFilePath, "MD");
        FileUtil.writeLines(wordMarkDownList, wordMdFilePath, StandardCharsets.UTF_8);
        fileStoreRepository.save(localFile);


        List<String> pdfMarkDownList = result.getPdfProcess().getMardDownList();
        String pdfMdId =  UUID.randomUUID().toString();
        String pdfMdFilePath = baseDir + pdfMdId +".md";
        FileStore pdfLocalFile = FileStore.createLocalFile(pdfMdId, pdfMdFilePath, "MD");
        FileUtil.writeLines(pdfMarkDownList, pdfMdFilePath, StandardCharsets.UTF_8);
        fileStoreRepository.save(pdfLocalFile);


        result.setStdFileId(fileId);
        result.setCmpFileId(pdfMdId);

        ContractDiffTaskDetail detail = diffTaskDetailRepository.findByRelTaskId(6L);
        detail.setCompareResult(JSONObject.toJSONString(result));
        diffTaskDetailRepository.updateById(detail);

        System.out.println(JSONObject.toJSONString(result));
    }

    @Test
    public void wordFormatTest() {
        String path = "/Users/xuewenke/doc-diff-server/process/markdown/a91b1188-cc50-462e-952d-ad685abf9660/a91b1188-cc50-462e-952d-ad685abf9660.md";
        wordService.formatShowMarkdown(path);
    }

    @Test
    public void pdfFormatTest() {
        String path = "/Users/xuewenke/doc-diff-server/process/markdown/b2b4a8f7-42b1-49f7-adc6-68d159573100/auto/b2b4a8f7-42b1-49f7-adc6-68d159573100.md";
        String wordPath = localTempFilePath + "compare/table/word.md";
        List<String> wordMdLine = FileUtil.readLines(wordPath, StandardCharsets.UTF_8);
        wordMdLine =  wordMdLine.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
        Set<String> tableHeads = HtmlUtils.getTableHeads(wordMdLine);
        pdfService.formatShowMarkdown(path, tableHeads);
    }


    @Test
    public void mergeHtmlTableTest(){
        String pdfPath = "/Users/xuewenke/code/DID/web-server/doc-diff-server/src/main/temp-file/compare/table/pdf.md";
        String wordPath = "/Users/xuewenke/code/DID/web-server/doc-diff-server/src/main/temp-file/compare/table/word.md";
        List<String> readLines = FileUtil.readLines(pdfPath, StandardCharsets.UTF_8);
        List<String> wordMdLine = FileUtil.readLines(wordPath, StandardCharsets.UTF_8);

        Set<String> tableHeads = HtmlUtils.getTableHeads(wordMdLine);


        List<String> mergeHtmlTable = pdfService.mergeHtmlTable(readLines, tableHeads);

        List<String> tableLine = mergeHtmlTable.stream()
                .filter(line -> StrTools.isHtmlTable(line))
                .collect(Collectors.toList());

        System.out.println("mergeHtmlTable:" + tableLine.size());

//        mergeHtmlTable.forEach(System.out::println);
    }


}
