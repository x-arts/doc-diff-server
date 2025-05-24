package com.did.docdiffserver.data.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "contract_diff_task")
public class ContractDiffTask {


    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskName;

    private String standardFileId;

    private String compareFileId;

    private Date createTime;

}
