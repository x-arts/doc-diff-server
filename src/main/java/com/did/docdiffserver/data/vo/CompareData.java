package com.did.docdiffserver.data.vo;

import lombok.Data;

import java.util.List;

@Data
public class CompareData {

    private String compareText;
    private List<Integer> lineNumbers;


    public CompareData(String compareText, List<Integer> lineNumbers) {
        this.compareText = compareText;
        this.lineNumbers = lineNumbers;
    }
}
