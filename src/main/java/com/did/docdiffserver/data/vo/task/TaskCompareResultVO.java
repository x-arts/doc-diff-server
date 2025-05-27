package com.did.docdiffserver.data.vo.task;

import com.alibaba.fastjson2.annotation.JSONField;
import com.did.docdiffserver.data.vo.DiffResultItemVo;
import com.did.docdiffserver.data.vo.DiffTextItemVO;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TaskCompareResultVO {

    private String stdFileContent;

    private String cmpFileContent;

    private Map<String, DiffTextItemVO> detail;


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
                String tagLine = String.format("@%s@%s@%s@", insertIndex, line, insertIndex);
                wordMdList.set(wordLine, tagLine);
            }

            List<Integer> pdfLines = diffTextItemVO.getModifyLineNumbers();
            for (int pdfLine : pdfLines) {
                String line = pdfMdList.get(pdfLine);
                String tagLine = String.format("@%s@%s@%s@", insertIndex, line, insertIndex);
                pdfMdList.set(pdfLine,tagLine);
            }

            this.detail.put(insertIndex, diffTextItemVO);
            index++;
        }

    }

}
