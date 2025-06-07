package com.did.docdiffserver.data.vo.task;

import com.alibaba.fastjson2.annotation.JSONField;
import com.did.docdiffserver.data.vo.DiffItem;
import com.did.docdiffserver.data.vo.DiffResultItemVo;
import com.did.docdiffserver.data.vo.DiffTextItemVO;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.table.DiffTableFlag;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class TaskCompareResultVO {

    private String stdFileContent;

    private String cmpFileContent;

    private Map<String, DiffItem> detail;

    private String stdFileId;

    private String cmpFileId;


    @JSONField(serialize = false)
    @JsonIgnore
    private WordProcessVO wordProcess;

    @JSONField(serialize = false)
    @JsonIgnore
    private PdfProcessVO pdfProcess;

    @JSONField(serialize = false)
    @JsonIgnore
    private DiffResultItemVo diffResultItem;


    public static TaskCompareResultVO createResult(WordProcessVO word, PdfProcessVO pdf, DiffResultItemVo diffResultItem) {
        TaskCompareResultVO result = new TaskCompareResultVO();
        result.setWordProcess(word);
        result.setPdfProcess(pdf);
        result.setDiffResultItem(diffResultItem);
        result.setDetail(new HashMap<>());
        return result;
    }


    public void buildDetail() {
        List<String> wordMdList = wordProcess.getMarkDownList();
        List<String> pdfMdList = pdfProcess.getMardDownList();

        List<DiffTextItemVO> diffItemList = diffResultItem.getDiffItemList();

        int index = 1;
        for (DiffTextItemVO diffTextItemVO : diffItemList) {
            String insertIndex = index + "0";

            List<Integer> wordLines = diffTextItemVO.getOriginalLineNumbers();
            for (int wordLine : wordLines) {
                String line = wordMdList.get(wordLine);
                log.info("word@line index: {}, line: {}", wordLine, line);
                String tagLine = String.format("@%s@%s@%s@", insertIndex, line, insertIndex);
                wordMdList.set(wordLine, tagLine);
            }

            List<Integer> pdfLines = diffTextItemVO.getModifyLineNumbers();
            for (int pdfLine : pdfLines) {
                String line = pdfMdList.get(pdfLine);
                log.info("pdf@line index: {}, line: {}", pdfLine, line);
                String tagLine = String.format("@%s@%s@%s@", insertIndex, line, insertIndex);
                pdfMdList.set(pdfLine,tagLine);
            }

            this.detail.put(insertIndex, diffTextItemVO);
            index++;
        }

        //  表格的差异处理
        List<TableInfo> wordTables = wordProcess.getTableInfoList();
        Map<String, Integer> wordTableLineMap = wordProcess.getTableLineIndex();
        for (TableInfo wordTable : wordTables) {
            int tableLine = wordTableLineMap.get(wordTable.getTableId());
            wordMdList.set(tableLine, wordTable.toHtmlTable());
        }

        List<TableInfo> pdfTables = pdfProcess.getTableInfoList();
        Map<String, Integer> pdfTableLineMap = pdfProcess.getTableLineIndex();
        for (TableInfo pdfTable : pdfTables) {
            int tableLine = pdfTableLineMap.get(pdfTable.getTableId());
            pdfMdList.set(tableLine, pdfTable.toHtmlTable());
        }


        for (DiffTableFlag diffTableFlag : diffResultItem.getDiffTableItemList()) {
            this.detail.put(String.valueOf(diffTableFlag.getFlagIndex()), diffTableFlag);
        }

    }

}
