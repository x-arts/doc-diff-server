package com.did.docdiffserver.service;

import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.service.compent.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 *  pdf 文档处理
 */
@Slf4j
@Service
public class PdfService {


    @Resource
    private StoreService storeService;

    public PdfProcessVO process(String filePath, String fileId) {
        PdfProcessVO processVO = PdfProcessVO.init(filePath, fileId);

        String markdownFilePath = storeService.getPdfMarkDownFilePath(fileId);
        processVO.process(markdownFilePath);
        return processVO;
    }
}
