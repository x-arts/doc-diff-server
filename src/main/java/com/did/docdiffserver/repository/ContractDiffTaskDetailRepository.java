package com.did.docdiffserver.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.did.docdiffserver.data.entity.ContractDiffTaskDetail;
import com.did.docdiffserver.repository.mapper.ContractDiffTaskDetailMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ContractDiffTaskDetailRepository extends ServiceImpl<ContractDiffTaskDetailMapper, ContractDiffTaskDetail> {

    /**
     * 根据任务ID查询任务详情
     *
     * @param relTaskId 任务ID
     * @return 任务详情
     */
    public ContractDiffTaskDetail findByRelTaskId(Long relTaskId) {
        return this.lambdaQuery()
                .eq(ContractDiffTaskDetail::getRelTaskId, relTaskId)
                .one();
    }
}
