package com.did.docdiffserver.service;

import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 *  pdf 文档处理
 */
@Slf4j
@Service
public class PdfProcessService {

    public PdfProcessVO process(String filePath, String fileId) {
        PdfProcessVO processVO = PdfProcessVO.init(filePath, fileId);
        processVO.process();
        return processVO;
    }
}
