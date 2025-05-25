package com.did.docdiffserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FormatService {


    /**
     * 符号优化
     *
     * @param lines
     * @return
     */
    public List<String> symbolicFormat(List<String> lines) {
        List<String> formatLines = new ArrayList<>(lines.size());
        for (String line : lines) {
            String replace = line.replace("(", "（")
                    .replace(")", "）");

            // 冒号替换
            replace = replace.replace(":", "：")
                    .replace("□", "")
                    .replace("\uF06C", "")
                    .replace("■", "");

            formatLines.add(replace);
        }

        return formatLines;
    }


}
