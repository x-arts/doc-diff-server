package com.did.docdiffserver.data.vo.table;

import lombok.Data;

@Data
public class HtmlTableContent {


    private String  html;
    private int index;



    public HtmlTableContent(String html, int index) {
        this.html = html;
        this.index = index;
    }


    public static HtmlTableContent ofSplit() {
        return new HtmlTableContent("|", 0);
    }


    public boolean isSplit(){
        return "|".equals(html);
    }

    public boolean isTable(){
        return !isSplit();
    }
}
