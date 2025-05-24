package com.did.docdiffserver.data.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

@Data
public class BasePageVO {

    private Integer total;
    private Integer pageSize;
    private Integer current;


    public void setPage(IPage<?> page) {
        this.setTotal((int) page.getTotal());
        this.setPageSize((int) page.getSize());
        this.setCurrent((int) page.getCurrent());
    }

}
