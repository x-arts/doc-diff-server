package com.did.docdiffserver.controller;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.did.docdiffserver.data.condition.MdTextDiffCondition;
import com.did.docdiffserver.data.vo.BaseResponseVo;
import com.did.docdiffserver.data.vo.ProcessFileResponseVO;
import com.did.docdiffserver.service.compent.DocCovertService;
import com.did.docdiffserver.service.DocDiffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/md-diff-api")
public class MdDiffController {


    @Value("${local.file-upload-path}")
    private String uploadFilePath;

    @Resource
    private DocDiffService mdDiffService;

    @Resource
    private DocCovertService docCovertService;


    private static Set<String> allowedFileTypes = new HashSet<>();

    static {
        allowedFileTypes.add("pdf");
        allowedFileTypes.add("docx");
    }

    @PostMapping("/mdTextDiff")
    public String mdTextDiff(@RequestBody MdTextDiffCondition condition) {
        log.info("mdTextDiff: condition = {}", JSONObject.toJSONString(condition));
        return mdDiffService.mdTextDiff(condition.getOldText(), condition.getNewText());
    }

    @PostMapping("/mdFileDiff")
    public String mdFileDiff(@RequestBody MdTextDiffCondition condition) {
        log.info("mdFileDiff: condition = {}", JSONObject.toJSONString(condition));
        return mdDiffService.mdTextDiff(condition.getOldText(), condition.getNewText());
    }

    @GetMapping("/getKeywords")
    public ResponseEntity<JSONArray> getKeywords(){
        JSONArray keyWords = new JSONArray("河南公司新能源","设备");
        return ResponseEntity.ok(keyWords);
    }



    @PostMapping("/mark")
    public ResponseEntity<BaseResponseVo> mark(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponseVo.nSuccess("文件不能为空！"));
        }

        // 指定保存路径
        String uploadDir = uploadFilePath;
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs(); // 自动创建目录
        }
        log.info("uploadFile: getOriginalFilename={}", file.getOriginalFilename());
        String suffix = FileNameUtil.getSuffix(file.getOriginalFilename());
        assert suffix != null;
        if (!allowedFileTypes.contains(suffix)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponseVo.nSuccess("仅支持 docx，pdf 格式"));
        }

        String fileId = UUID.randomUUID().toString();

        // 保存文件
        try {
            String filePath = uploadDir + fileId + "." + suffix;
            file.transferTo(new File(filePath));
            String text = docCovertService.docx2HtmlAndGet(filePath, fileId);
            ProcessFileResponseVO processFileVO = new ProcessFileResponseVO();
            processFileVO.setFileId(fileId);
            processFileVO.setText(text);
            processFileVO.successStatus();
            return ResponseEntity.ok(processFileVO);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponseVo.nSuccess("上传失败"));
        }
    }



    @PostMapping("/upload")
    public ResponseEntity<BaseResponseVo> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponseVo.nSuccess("文件不能为空！"));
        }

        // 指定保存路径
        String uploadDir = uploadFilePath;
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs(); // 自动创建目录
        }
        log.info("uploadFile: getOriginalFilename={}", file.getOriginalFilename());
        String suffix = FileNameUtil.getSuffix(file.getOriginalFilename());
        assert suffix != null;
        if (!allowedFileTypes.contains(suffix)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponseVo.nSuccess("仅支持 docx，pdf 格式"));
        }

        String fileId = UUID.randomUUID().toString();

        // 保存文件
        try {
            String filePath = uploadDir + fileId + "." + suffix;
            file.transferTo(new File(filePath));
//            String text = docCovertService.doc2md(fileId);
            ProcessFileResponseVO processFileVO = new ProcessFileResponseVO();
            processFileVO.setFileId(fileId);
//            processFileVO.setText(text);
            processFileVO.successStatus();
            return ResponseEntity.ok(processFileVO);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponseVo.nSuccess("上传失败"));
        }
    }

}
