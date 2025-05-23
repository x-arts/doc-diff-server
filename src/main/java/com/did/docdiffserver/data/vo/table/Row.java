package com.did.docdiffserver.data.vo.table;

import cn.hutool.core.util.StrUtil;
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

    public String simpleRowLine() {
        List<String> cellTexts = new ArrayList<>();
        for (Cell cell : cells) {
            cellTexts.add(cell.simpleText());
        }
        return StrUtil.join("|", cellTexts);
    }

    /**
     * 判断是否有空列
     * @return
     */
    public boolean onlyLastOneCellNotEmpty() {

        String lastText = cells.get(cells.size() - 1).getText();
        boolean lastNotBlank = StrUtil.isNotBlank(lastText);

        boolean othersIsBlank = true;

        for (int i = 0; i < cells.size() - 1; i++) {
            String text = cells.get(i).getText();
            if (StrUtil.isNotBlank(text)) {
                othersIsBlank = false;
                break;
            }
        }
        return lastNotBlank && othersIsBlank;
    }

}
