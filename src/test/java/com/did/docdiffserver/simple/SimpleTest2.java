package com.did.docdiffserver.simple;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleTest2 {


    @Test
    public void simpleTest2(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(3);
        list.add(5);
        list.add(7);
        list.add(9);

        int key = 9;
        int index = Collections.binarySearch(list, key);

        if (index >= 0) {
            System.out.println("找到元素，索引位置: " + index);
        } else {
            System.out.println("未找到元素，可插入位置: " + (-index - 1));
        }

    }
}
