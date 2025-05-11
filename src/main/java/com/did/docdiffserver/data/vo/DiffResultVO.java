package com.did.docdiffserver.data.vo;

import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import lombok.Data;

import java.util.List;

/**
 * @author xuewenke
 * @since 2025/5/12 00:53
 */
@Data
public class DiffResultVO {

    private WordProcessVO wordProcess;

    private PdfProcessVO pdfProcess;

    private List<String> originalList;

    private List<String> modifyList;


    public static  DiffResultVO create(WordProcessVO word, PdfProcessVO pdf, List<String> original, List<String> modify) {
        DiffResultVO diffResultVO = new DiffResultVO();
        diffResultVO.setWordProcess(word);
        diffResultVO.setPdfProcess(pdf);
        diffResultVO.setOriginalList(original);
        diffResultVO.setModifyList(modify);
        return diffResultVO;
    }

}