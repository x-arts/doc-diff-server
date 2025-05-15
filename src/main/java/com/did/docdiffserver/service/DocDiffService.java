package com.did.docdiffserver.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.vo.DiffResultVO;
import com.did.docdiffserver.data.vo.SimilarSearchResult;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import com.did.docdiffserver.service.compent.StoreService;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;
import toolgood.words.StringSearch;

import javax.annotation.Resource;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DocDiffService {

    @Resource
    private WordService wordService;

    @Resource
    private PdfService pdfService;

    @Resource
    private StoreService storeService;


    /**
     * 上传两个文件 id， 对文件对比
     * @param wordFileId
     * @param pdfFileId
     * @return
     */
    public String docDiff(String wordFileId, String pdfFileId) {
        WordProcessVO wordProcess = wordService.process(storeService.getWordMarkDownFilePath(wordFileId), wordFileId);
        log.info("docDiff wordProcess  finish ");
        PdfProcessVO pdfProcess = pdfService.process(storeService.getPdfMarkDownFilePath(pdfFileId), pdfFileId);
        log.info("docDiff pdfProcess  finish ");
        DiffResultVO diff = findDiff(wordProcess, pdfProcess);
        log.info("docDiff findDiff  finish ");
        log.info("docDiff  diff size = {}", diff.getOriginalList().size());
        printSideBySide(diff);
        return generateUnifiedDiff(diff);
    }

    private void  printSideBySide(DiffResultVO diff){
        List<String> originalList = diff.getOriginalList();

        for (String string : originalList) {
            System.out.println(string);
        }

        System.out.println("==========================");

        List<String> modifyList = diff.getModifyList();
        for (String string : modifyList) {
            System.out.println(string);
        }

    }




    private DiffResultVO findDiff(WordProcessVO wordProcess, PdfProcessVO pdfProcess) {
        List<String> original = new ArrayList<>();
        List<String> modify = new ArrayList<>();
        String dict = pdfProcess.getCompareDict();
        log.info("findDiff dict = {}", dict.length());

        for (String line : wordProcess.getCompareMarkdownList()) {
            oneLineFindDiff(original, modify, dict, line);
        }

        log.info("findDiff finish = {}", original);
        return DiffResultVO.create(wordProcess, pdfProcess, original, modify);
    }


    public void oneLineFindDiff(List<String> original, List<String> modify, String dict, String findKye) {
//        log.info("oneLineFindDiff findKye = {}", findKye);
        if (StrUtil.isBlank(findKye)) {
            return;
        }
        boolean findResult = preciseSearch(dict, findKye);
        if (!findResult) {
            // 相似度匹配
            List<SimilarSearchResult> searchResults = similarSearch(dict, findKye);
            Optional<SimilarSearchResult> max = searchResults.stream().max(Comparator.comparingDouble(SimilarSearchResult::getScore));
            if (max.isPresent()) {
                original.add(max.get().getSimilarStr());
                modify.add(findKye);
            }
        }

    }





    private  List<SimilarSearchResult>  similarSearch(String text, String key) {
        JaroWinklerSimilarity jw = new JaroWinklerSimilarity();
        double threshold = 0.85;  // 相似度阈值

        List<SimilarSearchResult> searchResults = new ArrayList<>();
        int lenB = key.length();
        // 遍历所有长度与 B 相同的滑动窗口子串
        for (int i = 0; i + lenB <= text.length(); i++) {
            String sub = text.substring(i, i + lenB);
            Double score = jw.apply(sub, key);
            if (score != null && score > threshold) {
                searchResults.add(new SimilarSearchResult(sub, score));
            }
        }
        return searchResults;
    }



    private boolean preciseSearch(String text, String key) {
        StringSearch search = new StringSearch();
        search.SetKeywords(CollectionUtil.newArrayList(key));
        List<String> found = search.FindAll(text);
        if (CollectionUtil.isEmpty(found)) {
            return false;
        }
        return true;
    }



    public String mdTextDiff(String oldText, String newText) {
        return generateUnifiedDiff(oldText, newText, "old.md", "new.md");
    }


    public String generateUnifiedDiff(DiffResultVO diffResult){

        List<String> originalList = diffResult.getOriginalList();
        // 生成差异补丁
        Patch<String> patch = DiffUtils.diff(originalList, diffResult.getModifyList());

        // 2. 用 UnifiedDiffUtils 生成 unified diff 内容
        List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(
                "original.txt",   // 原文件名
                "revised.txt",    // 新文件名
                originalList,         // 原内容
                patch,            // Patch对象
                3                 // 上下文行数
        );

        return String.join(System.lineSeparator(), unifiedDiff);
    }



    /**
     * 生成标准 Unified Diff 格式文本
     *
     * @param original 原始文本
     * @param revised  修改后文本
     * @param fromFile 原始文件名（显示在---行）
     * @param toFile   修改文件名（显示在+++行）
     * @return diff格式字符串
     */
    public String generateUnifiedDiff(
            String original,
            String revised,
            String fromFile,
            String toFile) {

        // 分割文本为行列表
        List<String> originalLines = Arrays.asList(original.split("\\R"));
        List<String> revisedLines = Arrays.asList(revised.split("\\R"));

        // 生成差异补丁
        Patch<String> patch = DiffUtils.diff(originalLines, revisedLines);

        // 构建 Diff 输出
        StringWriter stringWriter = new StringWriter();


        // 2. 用 UnifiedDiffUtils 生成 unified diff 内容
        List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(
                "original.txt",   // 原文件名
                "revised.txt",    // 新文件名
                originalLines,         // 原内容
                patch,            // Patch对象
                3                 // 上下文行数
        );
        // 将 List<String> 转换为单个字符串

        //        log.info("unifiedDiffString: {}", unifiedDiffString);

        return String.join(System.lineSeparator(), unifiedDiff);
    }


}
