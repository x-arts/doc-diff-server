package com.did.docdiffserver.data.enums;

import lombok.Getter;

/**
 * 任务处理状态
 *
 * @author 45112
 */
//处理状态：PROCESS_SUCCESS-处理成功，PROCESS_FAIL-处理失败，NODE_FAIL-节点失败，WAITING_PROCESS-未处理，PROCESSING-处理中，ROLLBACK_SUCCESS-回退成功
@Getter
public enum TaskProcessStatus {

    PROCESS_SUCCESS("PROCESS_SUCCESS", "处理成功"),
    PROCESS_FAIL("PROCESS_FAIL", "处理失败"),
    WAITING_PROCESS("WAITING_PROCESS", "未处理"),
    PROCESSING("PROCESSING", "处理中"),
    ;

    public final String code;
    public final String name;

    TaskProcessStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
