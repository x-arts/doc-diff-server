package com.did.docdiffserver.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class YamlConfig {


    @Value("${local.file-upload-path}")
    private String uploadBase;

    @Value("${local.markdown}")
    private String markdownBasePath;

    @Value("${local.script}")
    private String scriptBasePath;


}
