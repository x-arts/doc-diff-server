package com.did.docdiffserver.data.condition;

import com.did.docdiffserver.data.vo.table.TableInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author xuewenke
 * @since 2025/5/22 00:13
 */
@Data
public class TableCompareCondition {


    private List<TableInfo> wordTableInfoList;

    private List<TableInfo> pdftableInfoList;


}