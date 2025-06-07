package com.did.docdiffserver.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.config.StoreConfig;
import com.did.docdiffserver.data.vo.*;
import com.did.docdiffserver.data.vo.pdf.PdfProcessVO;
import com.did.docdiffserver.data.vo.table.DiffTableFlag;
import com.did.docdiffserver.data.vo.table.TableInfo;
import com.did.docdiffserver.data.vo.task.TaskCompareResultVO;
import com.did.docdiffserver.data.vo.word.WordProcessVO;
import com.did.docdiffserver.utils.MergeTableUtils;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;
import toolgood.words.StringSearch;

import javax.annotation.Resource;
import java.io.StringWriter;
import java.util.*;

@Slf4j
@Service
public class DocDiffService {

    @Resource
    private WordService wordService;

    @Resource
    private PdfService pdfService;

    @Resource
    private StoreConfig storeConfig;

    @Resource
    private TableContentCompareService tableContentCompareService;



    public TaskCompareResultVO docDiffTask(WordProcessVO wordProcess, PdfProcessVO pdfProcess) {
        // 文档比对
        DiffResultVO diff = findDiff(wordProcess, pdfProcess);
        log.info("docDiffTask textDiff  finish ");


        // 表格比对
        List<DiffTableFlag> tableDiffs = new ArrayList<>();

//        tableDiffs = tableDiff(wordProcess, pdfProcess);

        log.info("docDiffTask tableDiff  finish ");

        // 生成差异补丁+ 生成统一的diff文本(打上 @ 标记)
        DiffResultItemVo diffResultItemVo = DiffResultItemVo.of(diff.getDiffTextList(), tableDiffs);
        TaskCompareResultVO result = TaskCompareResultVO.createResult(wordProcess, pdfProcess, diffResultItemVo);
        result.buildDetail();
        return result;
    }


    private List<DiffTableFlag> tableDiff(WordProcessVO wordProcess, PdfProcessVO pdfProcess) {
        List<DiffTableFlag> tableDiffs = new ArrayList<>();

        List<TableInfo> wordTableInfos = wordProcess.getTableInfoList();
        List<TableInfo> pdfTableInfos = pdfProcess.getTableInfoList();

        int startIndex = 4000;
        for (int i = 0; i < wordTableInfos.size(); i++) {
            TableInfo wordTableInfo = wordTableInfos.get(i);
            TableInfo pdfTableInfo = pdfTableInfos.get(i);
            MergeTableUtils.mergeTableInfoRow(pdfTableInfo);
            List<DiffTableFlag> diffTableFlags = tableContentCompareService.compareTableContent(wordTableInfo, pdfTableInfo, startIndex + i);
            tableDiffs.addAll(diffTableFlags);
        }

        return tableDiffs;
    }


    /**
     * 上传两个文件 id， 对文件对比
     * @param wordFileId
     * @param pdfFileId
     * @return
     */
    public TaskCompareResultVO docDiff(String wordFileId, String pdfFileId) {
        WordProcessVO wordProcess = wordService.process(storeConfig.getWordMarkDownFilePath(wordFileId), wordFileId);
        log.info("docDiff wordProcess  finish ");
        PdfProcessVO pdfProcess = pdfService.process(storeConfig.getPdfMarkDownFilePath(pdfFileId), pdfFileId);
        log.info("docDiff pdfProcess  finish ");
        // 文档比对
        DiffResultVO diff = findDiff(wordProcess, pdfProcess);
        log.info("docDiff findDiff  finish ");


        List<DiffTableFlag> tableDiffs = new ArrayList<>();

//        List<TableInfo> wordTableInfos =  wordProcess.getTableInfoList();
//        List<TableInfo> pdfTableInfos =  pdfProcess.getTableInfoList();
//
//        int startIndex = 4000;
//        for (int i = 0; i < wordTableInfos.size(); i++) {
//            TableInfo wordTableInfo = wordTableInfos.get(i);
//            TableInfo pdfTableInfo = pdfTableInfos.get(i);
//            MergeTableUtils.mergeTableInfoRow(pdfTableInfo);
//            List<DiffTableFlag> diffTableFlags = tableContentCompareService.compareTableContent(wordTableInfo, pdfTableInfo, startIndex + i);
//            tableDiffs.addAll(diffTableFlags);
//        }

        log.info("docDiff tableDiff  finish ");

        DiffResultItemVo diffResultItemVo = DiffResultItemVo.of(diff.getDiffTextList(), tableDiffs);

        TaskCompareResultVO result = TaskCompareResultVO.createResult(wordProcess, pdfProcess, diffResultItemVo);
        result.buildDetail();

        return result;
    }






    private DiffResultVO findDiff(WordProcessVO wordProcess, PdfProcessVO pdfProcess) {

        DiffResultVO diffResultVO = DiffResultVO.create(wordProcess, pdfProcess);
        String dict = pdfProcess.getCompareDict();
        log.info("findDiff dict = {}", dict);

        CompareData currentCompareText = wordProcess.fetchCompareText();
        CompareData nextCompareText = wordProcess.fetchCompareText();
        NextTextMatchVO hadMatch = null;
        while (!nextCompareText.getCompareText().equals(WordProcessVO.END_LINE)) {
            if (hadMatch == null) {
                // 第一次为 null 需要自己组装
                String matchText = findMatchText(dict, currentCompareText.getCompareText());
                hadMatch = NextTextMatchVO.create(currentCompareText, matchText);
            }

            hadMatch = oneLineFindDiff(diffResultVO, hadMatch, nextCompareText);
            nextCompareText = wordProcess.fetchCompareText();
            log.info("findDiff nextCompareText = {}", nextCompareText);
        }

        return diffResultVO;
    }


    public NextTextMatchVO oneLineFindDiff(DiffResultVO diffResultVO, NextTextMatchVO hadMatch, CompareData findNext) {

        WordProcessVO wordProcess = diffResultVO.getWordProcess();
        PdfProcessVO pdfProcess = diffResultVO.getPdfProcess();
        String dict = pdfProcess.getDynamicDict();

        log.info("oneLineFindDiff findNext = {}", findNext);
        String matchTextNext = findMatchText(dict, findNext.getCompareText());
        log.info("oneLineFindDiff matchTextNext = {}", matchTextNext);

        if (hadMatch.isNotSame()) {

            if (hadMatch.isMatchTextEmpty()) {
                // 没有找到的情况
                diffResultVO.getOriginalList().add(hadMatch.getOriginalText().getCompareText());
                diffResultVO.getModifyList().add("");
                diffResultVO.addDiffItem(hadMatch.getOriginalText().getCompareText(), "", hadMatch.getOriginalText().getLineNumbers(), Collections.emptyList());

                return NextTextMatchVO.create(findNext, matchTextNext);
            }

            diffResultVO.getOriginalList().add(hadMatch.getOriginalText().getCompareText());
            String matchText = hadMatch.getMatchText();

            int startIndex = pdfProcess.getMatchTextIndex(pdfProcess.getCutIndex(),matchText);
            int endIndex;
            if(StrUtil.isBlank(matchTextNext)) {
                // 没有匹配到
                endIndex = startIndex + matchText.length();
            } else {
                endIndex = pdfProcess.getMatchTextIndex(startIndex,matchTextNext);
            }

            String modifyText = pdfProcess.getDictSubString(startIndex, endIndex);
            diffResultVO.getModifyList().add(modifyText);

            List<Integer> pdfLineNums = pdfProcess.fetchMatchIndex(modifyText);

            diffResultVO.addDiffItem(hadMatch.getOriginalText().getCompareText(), modifyText, hadMatch.getOriginalText().getLineNumbers(), pdfLineNums);

        }
        return NextTextMatchVO.create(findNext, matchTextNext);
    }


    public String findMatchText(String dict, String findKey) {
        List<String> findText = preciseSearch(dict, findKey);

        if (CollectionUtil.isNotEmpty(findText)) {
            return findText.get(0);
        }

        // 相似度匹配
        List<SimilarSearchResult> searchResults = similarSearch(dict, findKey);
        Optional<SimilarSearchResult> max = searchResults.stream().max(Comparator.comparingDouble(SimilarSearchResult::getScore));
        if (max.isPresent()) {
            return max.get().getSimilarStr();
        }
        return "";
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



    private List<String> preciseSearch(String text, String key) {
        StringSearch search = new StringSearch();
        search.SetKeywords(CollectionUtil.newArrayList(key));
        return search.FindAll(text);
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
