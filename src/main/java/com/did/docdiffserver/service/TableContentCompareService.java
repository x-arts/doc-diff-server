package com.did.docdiffserver.service;

import com.did.docdiffserver.data.vo.table.Cell;
import com.did.docdiffserver.data.vo.table.DiffTableFlag;
import com.did.docdiffserver.data.vo.table.Row;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.utils.MergeTableUtils;
import com.did.docdiffserver.utils.StrTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TableContentCompareService {

    /**
     * 比较两个表格的内容差异
     * @param originalTable 原始表格
     * @param modifiedTable 修改后的表格
     * @return 差异列表
     */
    public List<DiffTableFlag> compareTableContent(TableInfo originalTable, TableInfo modifiedTable, int startIndex) {
        List<DiffTableFlag> diffFlags = new ArrayList<>();

        // 获取两个表格的行数
        List<Row> originalRows = originalTable.getRows();
        List<Row> modifiedRows = modifiedTable.getRows();

        // 获取最大行数，用于遍历
        int maxRowCount = Math.max(
            originalRows.size(),
            modifiedRows.size()
        );

        int flagIndex = startIndex; // 用于生成唯一的flagIndex

        // 遍历所有行
        for (int rowIndex = 0; rowIndex < maxRowCount; rowIndex++) {
            Row originalRow = rowIndex < originalRows.size() ? originalRows.get(rowIndex) : null;
            Row modifiedRow = rowIndex < modifiedRows.size() ? modifiedRows.get(rowIndex) : null;

            // 如果原始行不存在，说明是新增的行
            if (originalRow == null && modifiedRow != null) {
                for (Cell modifiedCell : modifiedRow.getCells()) {
                    if (modifiedCell.getText() != null && !modifiedCell.getText().trim().isEmpty()) {
                        int insertIndex = flagIndex++;
                        modifiedCell.flagCell(insertIndex);
                        diffFlags.add(DiffTableFlag.create("", modifiedCell.getText(), insertIndex));
                    }
                }
                continue;
            }

            // 如果修改后的行不存在，说明是删除的行
            if (originalRow != null && modifiedRow == null) {
                for (Cell originalCell : originalRow.getCells()) {
                    if (originalCell.getText() != null && !originalCell.getText().trim().isEmpty()) {
                        int insertIndex = flagIndex++;
                        originalCell.flagCell(insertIndex);
                        diffFlags.add(DiffTableFlag.create(originalCell.getText(), "", insertIndex));
                    }
                }
                continue;
            }

            // 如果两行都存在，比较每一列的内容
            if (originalRow != null && modifiedRow != null) {
                List<Cell> originalCells = originalRow.getCells();
                List<Cell> modifiedCells = modifiedRow.getCells();

                int maxCellCount = Math.max(
                    originalCells.size(),
                    modifiedCells.size()
                );

                // 遍历所有列
                for (int cellIndex = 0; cellIndex < maxCellCount; cellIndex++) {

                    Cell originalCell = cellIndex < originalCells.size() ?
                            originalCells.get(cellIndex) : null;
                    Cell modifiedCell = cellIndex < modifiedCells.size() ?
                            modifiedCells.get(cellIndex) : null;

                    String originalText = Objects.nonNull(originalCell) ?
                            originalCell.getText() : "";
                    String modifiedText = Objects.nonNull(modifiedCell) ?
                            modifiedCell.getText() : "";

                    // 清理文本内容，移除空格等
                   String  originalCompareText = cleanText(originalText);
                   String  modifiedCompareText = cleanText(modifiedText);

                    // 如果内容不一致，创建差异标记,比对的字符串用去除格式的字符串比对
                    if (!originalCompareText.equals(modifiedCompareText)) {
                        int insertIndex = flagIndex++;

                        if (Objects.nonNull(originalCell)) {
                            originalCell.flagCell(insertIndex);
                        }

                        if (Objects.nonNull(modifiedCell)) {
                            modifiedCell.flagCell(insertIndex);
                        }


                        diffFlags.add(DiffTableFlag.create(originalText, modifiedText, insertIndex));
                    }
                }
            }
        }

        return diffFlags;
    }

    /**
     * 清理文本内容，移除空格等
     */
    private String cleanText(String text) {
        if (text == null) {
            return "";
        }

        text = StrTools.removeSpaceInLine(text);
        text = StrTools.replacePunctuation(text);
        return text.trim();
    }
}
