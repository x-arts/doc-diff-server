package com.did.docdiffserver.data;

import lombok.Data;

@Data
public class SimilarSearchResult {

    private String similarStr;

    private Double score;

    public SimilarSearchResult(String similarStr, Double score) {
        this.similarStr = similarStr;
        this.score = score;
    }
}
