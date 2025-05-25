package com.did.docdiffserver.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.did.docdiffserver.data.condition.TaskPageListCondition;
import com.did.docdiffserver.data.entity.ContractDiffTask;
import com.did.docdiffserver.repository.mapper.ContractDiffTaskMapper;
import org.springframework.stereotype.Repository;


@Repository
public class ContractDiffTaskRepository extends ServiceImpl<ContractDiffTaskMapper, ContractDiffTask> {


    public ContractDiffTask findByTaskId(String taskId) {
        return lambdaQuery().eq(ContractDiffTask::getTaskId, taskId).one();
    }

    public IPage<ContractDiffTask>  pageList(TaskPageListCondition pageCondition) {
        IPage<ContractDiffTask> page = new Page<>();
        page.setCurrent(pageCondition.getCurrent());
        page.setSize(pageCondition.getPageSize());
        return baseMapper.selectPage(page, null);
    }

}
