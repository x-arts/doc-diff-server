package com.did.docdiffserver.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.did.docdiffserver.data.entity.ContractDiffTask;
import com.did.docdiffserver.repository.mapper.ContractDiffTaskMapper;
import org.springframework.stereotype.Repository;


@Repository
public class ContractDiffTaskRepository extends ServiceImpl<ContractDiffTaskMapper, ContractDiffTask> {
}
