package com.did.docdiffserver;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = DocDiffServerApplication.class, value = {"spring.main.allow-bean-definition-overriding=true"})
public class TestBase {

    public static final String filePath = "/Users/xuewenke/temp-file/doc-diff-server/pretreatment/";
    public static final String localTempFilePath = "/Users/xuewenke/code/DID/web-server/doc-diff-server/src/main/temp-file/";
//    private static final String localTempFilePath =  "/Users/xuewenke/workspace/code/doc-diff-server/src/main/temp-file/";


}
