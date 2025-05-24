package com.did.docdiffserver.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;



@Data
@TableName(value = "file_store")
@Accessors(chain = true)
public class FileStore {


    @TableId(type = IdType.AUTO)
    private Long id;

    private String fileId;

    private String format;

    private String storeType;

    private Date createTime;

}
