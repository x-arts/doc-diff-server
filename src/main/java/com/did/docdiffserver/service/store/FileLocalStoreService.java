package com.did.docdiffserver.service.store;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.UUID;
import com.did.docdiffserver.config.StoreConfig;
import com.did.docdiffserver.data.entity.FileStore;
import com.did.docdiffserver.repository.FileStoreRepository;
import com.did.docdiffserver.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class FileLocalStoreService {

    @Resource
    private StoreConfig storeConfig;

    @Resource
    private FileStoreRepository fileStoreRepository;

    /**
     * 保存文件,存储到本地
     * @param uploadFile
     * @return
     */
    public String saveFile(MultipartFile uploadFile) throws IOException {
        String fileId = UUID.randomUUID().toString();
        String suffix = FileNameUtil.getSuffix(uploadFile.getOriginalFilename());
        AssertUtil.strNotBlank(suffix, "文件格式错误");

        String filePath = storeConfig.getUploadBase() + fileId + "." + suffix;
        uploadFile.transferTo(new File(filePath));

        FileStore localFile = FileStore.createLocalFile(fileId,filePath, suffix);
        fileStoreRepository.save(localFile);
        return fileId;
    }



}
