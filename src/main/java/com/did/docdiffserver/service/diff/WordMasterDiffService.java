package com.did.docdiffserver.service.diff;

import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.CompareData;
import com.did.docdiffserver.data.vo.DiffResultVO;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import com.did.docdiffserver.utils.DocDiffUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WordMasterDiffService {


    public DiffResultVO findDiff(WordProcessVO wordProcess, PdfProcessVO pdfProcess) {
        DiffResultVO diffResultVO = DiffResultVO.create(wordProcess, pdfProcess);
        String dict = pdfProcess.getCompareDict();
        log.info("findDiff dict = {}", dict);

        CompareData currentCompareText = wordProcess.fetchCompareText();

        while (!currentCompareText.getCompareText().equals(WordProcessVO.END_LINE)) {
            String originalText = currentCompareText.getCompareText();
            String matchText = DocDiffUtils.findMatchText(dict, currentCompareText.getCompareText());
            if (!StrUtil.equals(matchText, originalText)) {
                List<Integer> pdfLineNums = pdfProcess.fetchMatchIndex(matchText);
                diffResultVO.addDiffItem(originalText, matchText, currentCompareText.getLineNumbers(), pdfLineNums);
            }
            currentCompareText = wordProcess.fetchCompareText();
        }

        return diffResultVO;
    }



}
