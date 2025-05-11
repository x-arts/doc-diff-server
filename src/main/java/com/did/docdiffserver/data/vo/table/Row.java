package com.did.docdiffserver.data.vo.table;

import lombok.Data;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Data
public class Row {

    private List<Cell> cells;


    public static Row of(Element tr) {
        Elements tds = tr.select("td");
        List<Cell> cells = new ArrayList<>(tds.size());
        for (Element td : tds) {
            cells.add(Cell.of(td.text()));
        }
        Row row = new Row();
        row.setCells(cells);
        return row;
    }

}
