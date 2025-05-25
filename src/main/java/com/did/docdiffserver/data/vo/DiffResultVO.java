package com.did.docdiffserver.data.vo;

import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuewenke
 * @since 2025/5/12 00:53
 */
@Data
public class DiffResultVO {

    private WordProcessVO wordProcess;

    private PdfProcessVO pdfProcess;


    private List<DiffTextItemVO> diffTextList;


    private List<String> originalList;
//
    private List<String> modifyList;


    public void addDiffItem(String original, String modify, List<Integer> originalLineNumbers, List<Integer> modifyLineNums) {
         this.diffTextList.add(DiffTextItemVO.create(original, modify, originalLineNumbers, modifyLineNums));
    }






    public static  DiffResultVO create(WordProcessVO word, PdfProcessVO pdf) {
        DiffResultVO diffResultVO = new DiffResultVO();
        diffResultVO.setWordProcess(word);
        diffResultVO.setPdfProcess(pdf);
        diffResultVO.setOriginalList(new ArrayList<>());
        diffResultVO.setModifyList(new ArrayList<>());
        diffResultVO.setDiffTextList(new ArrayList<>());
        return diffResultVO;
    }

}