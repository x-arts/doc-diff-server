package com.did.docdiffserver.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "contract_diff_task_detail")
public class ContractDiffTaskDetail {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * task表的主键 id，用来关联
     */
    private Long relTaskId;

    /**
     * 标准文件 markdown 文件的 id
     */
    private String standardMarkdownFileId;

    /**
     * 预处理的文本
     */
    private String standardFormatText;

    /**
     * 比对文档markdown 文件 id
     */
    private String compareMarkdownFileId;

    /**
     * 比对预处理文本
     */
    private String compareFormatTxt;

    /**
     * 比对结果
     */
    private String compareResult;



    public static ContractDiffTaskDetail createForAdd(long relTaskId, String wordMdFileId, String compareMarkdownFileId) {
        ContractDiffTaskDetail detail = new ContractDiffTaskDetail();
        detail.setRelTaskId(relTaskId);
        detail.setStandardMarkdownFileId(wordMdFileId);
        detail.setCompareMarkdownFileId(compareMarkdownFileId);
        return detail;
    }

}
