package com.did.docdiffserver.service;

import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 *  pdf 文档处理
 */
@Slf4j
@Service
public class PdfService {


    public PdfProcessVO process(String markdownFilePath, String fileId) {
        PdfProcessVO processVO = PdfProcessVO.init(fileId, markdownFilePath);
        processVO.buildCompareData();
        return processVO;
    }
}
