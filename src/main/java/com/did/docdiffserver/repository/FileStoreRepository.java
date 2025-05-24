package com.did.docdiffserver.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.did.docdiffserver.data.entity.FileStore;
import com.did.docdiffserver.repository.mapper.FileStoreMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FileStoreRepository extends ServiceImpl<FileStoreMapper, FileStore> {
}
