package com.did.docdiffserver.simple;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.junit.Test;

import java.io.File;

public class TabulaTest {


    @Test
    public void pdfTextFetchTest() {

        String pdfPath = "/Users/xuewenke/temp-file/doc/ocrmypdf/scan.pdf";


        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            // 1. 初始化文本提取器
            PDFTextStripper stripper = new PDFTextStripper();

            // 2. 设置提取选项（可选）
            stripper.setSortByPosition(true); // 按页面物理顺序提取
            stripper.setStartPage(1);         // 起始页（从1开始）
            stripper.setEndPage(3);           // 结束页

            // 3. 提取文本
            String text = stripper.getText(document);
            System.out.println("提取的文本内容：\n" + text);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
