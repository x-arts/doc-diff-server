package com.did.docdiffserver.data.condition;

import lombok.Data;

@Data
public class MdTextDiffCondition {

    private String oldText;

    private String newText;


    private String oldFileId;

    private String newFileId;

}
