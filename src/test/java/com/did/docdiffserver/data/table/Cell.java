package com.did.docdiffserver.data.table;

import lombok.Data;

/**
 * @author xuewenke
 * @since 2025/5/8 22:39
 */
@Data
public class Cell {

    private String text;

    public static Cell of(String text) {
        if (text == null) {
            text = "";
        }
        Cell cell = new Cell();
        cell.text = text;

        return cell;

    }

}