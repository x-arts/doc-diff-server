package com.did.docdiffserver.data.vo.table;

import com.did.docdiffserver.utils.StrTools;
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


    public String simpleText(){
        String cellText = StrTools.removeSpaceInLine(this.getText());
        cellText = StrTools.replacePunctuation(cellText);
        return  cellText;
    }

}