package com.did.docdiffserver.simple;

import org.junit.Test;

import java.io.File;

public class SimpleTest2 {


    @Test
    public void simpleTest2(){

        File file = new File("/Users/xuewenke/temp-file/doc-diff-server/pretreatment/test.html");
        String parent = file.getParent();
        System.out.println(parent);

    }
}
