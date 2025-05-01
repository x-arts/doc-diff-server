package com.did.docdiffserver.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlMarkUtil {


    public static String highlightKeywords(String html, Set<String> keywords) {


        if (keywords.isEmpty()) return html;

        Document doc = Jsoup.parse(html);

        // 按长度降序排列避免短关键词优先匹配
        List<String> sortedKeywords = new ArrayList<>(keywords);
        sortedKeywords.sort((a, b) -> b.length() - a.length());

        // 构建正则表达式模式
        String patternStr = String.join("|", sortedKeywords.stream()
                .map(Pattern::quote) // 转义特殊字符
                .toArray(String[]::new));
        Pattern pattern = Pattern.compile(patternStr);

        // 使用遍历器处理所有文本节点
        doc.body().traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    processTextNode((TextNode) node, pattern);
                }
            }

            @Override
            public void tail(Node node, int depth) {
            }
        });

        return doc.html();
    }

    private static void processTextNode(TextNode textNode, Pattern pattern) {
        String text = textNode.text();
        Matcher matcher = pattern.matcher(text);
        List<Node> nodesToAdd = new ArrayList<>();
        int lastIndex = 0;

        while (matcher.find()) {
            // 添加匹配前的普通文本
            if (matcher.start() > lastIndex) {
                String plainText = text.substring(lastIndex, matcher.start());
                nodesToAdd.add(new TextNode(plainText));
            }

            // 创建高亮标签
            Element highlight = new Element("mark");
            highlight.attr("style", "background-color: lightskyblue");
            highlight.text(matcher.group()); // 保留原始大小写

            nodesToAdd.add(highlight);
            lastIndex = matcher.end();
        }

        // 处理剩余文本
        if (lastIndex < text.length()) {
            nodesToAdd.add(new TextNode(text.substring(lastIndex)));
        }

        // 替换原始文本节点
        if (!nodesToAdd.isEmpty()) {
            for (Node newNode : nodesToAdd) {
                textNode.before(newNode); // 关键修改点：顺序插入
            }
            textNode.remove();
        }
    }

    // 使用示例
    public static void main(String[] args) {
//        String html = "<html><body><p>This is a test string with foo and bar keywords.</p></body></html>";
//        Document doc = Jsoup.parse(html);
//
//        Set<String> keywords = new HashSet<>(Arrays.asList("test", "foo", "bar"));
//        highlightKeywords(doc, keywords);
//
//        System.out.println(doc.html());
    }

}
