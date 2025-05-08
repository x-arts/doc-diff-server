package com.did.docdiffserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {

    private String headersLine;

    private int columnSize;

    private List<String> rowDataLines;

}
