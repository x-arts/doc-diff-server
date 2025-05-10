package com.did.docdiffserver.simple;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.did.docdiffserver.data.Row;
import com.did.docdiffserver.data.SimilarSearchResult;
import com.did.docdiffserver.data.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import toolgood.words.StringSearch;

import java.util.*;

@Slf4j
public class SimpleTest {

    private static final String filePath = "/Users/xuewenke/temp-file/doc-diff-server/pretreatment/";

//    private static final String localTempFilePath =  "/Users/xuewenke/code/DID/web-server/doc-diff-server/src/main/temp-file/";
    private static final String localTempFilePath =  "/Users/xuewenke/workspace/code/doc-diff-server/src/main/temp-file/";



    private static Map<String, TableInfo> wordTable = new HashMap<>();

    /**
     * 简化 word md
     */
    @Test
    public void  simpleWordMd(){
        String wordMd2OneLinePath = wordMd2OneLine();
    }

    @Test
    public void  simplePdfMd(){
        String pdfMd2OneLinePath = pdfMd2OneLine();
    }


    /**
     * 从标准的 html 里获取表格数据
     */
    @Test
    public void fetchTableFromStandHtml() {
        String html = FileUtil.readString(localTempFilePath + "1/1.html", "utf-8");
        Document doc = Jsoup.parse(html);
        Elements tables = doc.select("table");
        for (Element table : tables) {
            String tableHeadersStr = getTableHeadersStr(table);
            log.info("tableHeadersStr = {}", tableHeadersStr);
        }
    }


    /**
     * 手里 word 的表格
     */
    @Test
    public void fetchTableFromWordMd() {
        String wordMdFilePath = localTempFilePath + "word-1.md";
        List<String> tableLines = FileUtil.readLines(wordMdFilePath, "utf-8");
        for (String tableLine : tableLines) {
            if (tableLine.startsWith("<html>")) {
                log.info("tableLine = {}", tableLine);
                Document doc = Jsoup.parse(tableLine);
                Element table = doc.select("table").first();
                collectionTableData(table);
            }
        }

        for (String s : wordTable.keySet()) {
            log.info("wordTableName = {}", s);
        }
    }

    /**
     * 获取表格数据
     * @param table
     */
    private void collectionTableData(Element table){
        TableInfo currentTable = getTableInfo(table);
        TableInfo tableInMap = wordTable.get(currentTable.getHeadersLine());
        if (tableInMap != null) {
            currentTable  = tableInMap;
        } else {
            wordTable.put(currentTable.getTableName(), currentTable);
        }
        List<Row> rows = currentTable.getRows();

        Elements trs = table.select("tr");
        int count = 0;
        for (Element tr : trs) {
            count++;
            if (count == 1) {
                continue;
            }
            rows.add(Row.of(tr));
        }
        currentTable.addRows(rows);
    }


    private TableInfo getTableInfo(Element table) {
        Element tr = table.select("tr").first();
        Elements tds = tr.select("td");
        int columnSize = tds.size();
        List<String> headers = new ArrayList<>();
        for (Element td : tds) {
            headers.add(td.text());
        }
        String headersLine = StrUtil.join("&", headers);
        return TableInfo.init(headersLine, columnSize );
    }




    private boolean containsEmptyTd(Elements tds) {
        for (Element td : tds) {
            if (StrUtil.isBlank(td.text().trim())) {
                return true;
            }
        }
        return false;
    }






    private String getTableHeadersStr(Element element) {
        Element tr = element.select("tr").first();
        Elements tds = tr.select("td");
        List<String> headers = new ArrayList<>();
        for (Element td : tds) {
//            log.info("header = {}", td.text());
            headers.add(td.text());
        }
        return StrUtil.join("&", headers);
    }



    private List<String> mergeTables(List<String> tableLines) {
        List<Elements> tables = new LinkedList<>();
        Elements currentTable = null;
        String currentTableHeads = null;
        for (String tableLine : tableLines) {
            Document doc = Jsoup.parse(tableLine);
            Elements table = doc.select("table");
            if (currentTable == null) {
                currentTable = table;
            }
        }

        return null;
    }


    private String getTableHeads(Elements table) {
        return null;
    }



    @Test
    public void  simplePdfMdFindDiff() {
        String wordSimpleMdFilePath = localTempFilePath + "word-1-simple.md";
        String pdfSimpleMdFilePath = localTempFilePath + "pdf-1-simple.md";
        List<String> wordLines = FileUtil.readLines(wordSimpleMdFilePath, "utf-8");
        List<String> pdfLines = FileUtil.readLines(pdfSimpleMdFilePath, "utf-8");
        String bigWord = StrUtil.join("", wordLines);
//        FileUtil.writeString(bigWord,localTempFilePath + "word-one-line.txt", "utf-8");

        List<String> wordDiffs = new LinkedList<>();
        List<String> pdfDiffs = new LinkedList<>();

        for (String pdfLine : pdfLines) {
            String findKey = pdfLine;
            boolean findResult = preciseSearch(bigWord, findKey);
            log.info("simplePdfMdFindDiff   findResult = {}", findResult);
            if (findResult) {
                //获取子字符串的下标
//                int index = bigWord.indexOf(findKey);
//                log.info("simplePdfMdFindDiff  index = {}", index);
            }  else {
                // 相似度匹配
                List<SimilarSearchResult> searchResults = similarSearch(bigWord, findKey);
//                for (SimilarSearchResult searchResult : searchResults) {
//                    log.info("simplePdfMdFindDiff  searchResult = {}", JSONObject.toJSONString(searchResult));
//                }
                Optional<SimilarSearchResult> max = searchResults.stream().max(Comparator.comparingDouble(SimilarSearchResult::getScore));
                if (max.isPresent()) {
                    wordDiffs.add(max.get().getSimilarStr());
                    pdfDiffs.add(findKey);
//                    log.info("simplePdfMdFindDiff  max = {}", JSONObject.toJSONString(max.get()));
                }
            }
        }

        for (String wordDiff : wordDiffs) {
            System.out.println(wordDiff);
        }
        System.out.println("=========================");
        for (String pdfDiff : pdfDiffs) {
            System.out.println(pdfDiff);
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
        for (String string : found) {
            log.info("preciseSearch match word = {}", string);
        }
        return true;
    }




    @Test
    public void  simpleTest() {
//        String wordMd2OneLinePath = wordMd2OneLine();
        String pdfMd2OneLinePath = pdfMd2OneLine();

//        List<String> wordLines = FileUtil.readLines(wordMd2OneLinePath, "utf-8");
//        List<String> pdfLines = FileUtil.readLines(pdfMd2OneLinePath, "utf-8");

//        // 配置行内差异生成器
//        DiffRowGenerator generator = DiffRowGenerator.create()
//                .showInlineDiffs(true)
//                .inlineDiffByWord(false)
//                .oldTag(f -> "[DEL]")  // 自定义旧文本标记
//                .newTag(f -> "[INS]")  // 自定义新文本标记
//                .build();
//
//        // 比较单行文本
//        List<DiffRow> rows = generator.generateDiffRows(
//                CollectionUtil.newArrayList(String.join("",wordLines)) ,
//                CollectionUtil.newArrayList(String.join("",pdfLines)));
//
//        // 输出差异结果
//        for (DiffRow row : rows) {
//            System.out.println("旧行标记: " + row.getOldLine());
//            System.out.println("新行标记: " + row.getNewLine());
//        }

    }


    private String wordMd2OneLine() {
        String wordMdFilePath = filePath + "word-1.md";
        String outputFilePath = filePath + "word-1-simple.md";
        if (!FileUtil.exist(outputFilePath)) {
            FileUtil.newFile(outputFilePath);
        }
        doMd2OneLine(wordMdFilePath, outputFilePath);
        return outputFilePath;
    }

    private String pdfMd2OneLine() {
        String mdFilePath = filePath + "pdf-1.md";
        String outputFilePath = filePath + "pdf-1-simple.md";
        if (!FileUtil.exist(outputFilePath)) {
            FileUtil.newFile(outputFilePath);
        }
        doMd2OneLine(mdFilePath, outputFilePath);
        return outputFilePath;
    }


    private void doMd2OneLine(String inputFilePath, String outputFilePath) {
        List<String> mdLines = FileUtil.readLines(inputFilePath, "utf-8");
        List<String> newLines = new LinkedList<>();
        for (String mdLine : mdLines) {
            String addLine = mdLine.trim();
            addLine = addLine.replaceAll("\\s+", "");
            if (StrUtil.isBlank(mdLine)) {
                continue;
            }
            if (addLine.startsWith("#")){
                addLine = addLine.replaceAll("#", "");
            }

            if (addLine.startsWith("<html>")) {
                continue;
            }

            //  目的是把目录去掉。单这个方式不一定精准
            if(addLine.matches("^[0-9].*")) {
                continue;
            }
            newLines.add(addLine);
        }
//        String oneLineMd = String.join("", newLines);
//        FileUtil.writeString(oneLineMd, outputFilePath, "utf-8");
        FileUtil.writeLines(newLines, outputFilePath, "utf-8");
    }

}
