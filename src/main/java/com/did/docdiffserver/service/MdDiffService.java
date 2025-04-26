package com.did.docdiffserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Service
public class MdDiffService {

    public String mdTextDiff(String oldText, String newText) {
        log.info("mdTextDiff: oldText={}, newText={}", oldText, newText);
        return "mdTextDiff: oldText=" + oldText + ", newText=" + newText;
    }


}
