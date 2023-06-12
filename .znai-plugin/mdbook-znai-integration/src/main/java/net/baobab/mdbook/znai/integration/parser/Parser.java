package net.baobab.mdbook.znai.integration.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Integer.parseInt;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class Parser {

    private static void gererateContentFile(String code) {
        (new File("./.znai-plugin/znai/index.md")).delete();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./.znai-plugin/znai/index.md"))) {
            writer.write((new JSONArray("[\"" + code + "\"]")).getString(0));
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String execCmd(String[] cmd, String[] envp, File dir) {
        try {
            Scanner s = new Scanner(Runtime.getRuntime().exec(cmd, envp, dir).getInputStream()).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    private static void generateZnaiDoc() {
        String[] cmd = {"../bin/znai/znai", "--doc-id", "docs", "--deploy", "../dist"};
        execCmd(cmd, null, new File(".znai-plugin/znai"));
    }

    private static String crawler() {
        String[] cmd = {"/usr/bin/node", "./index.js"};
        return execCmd(cmd, null, new File(".znai-plugin/bin/crawler/"));
    }

    private static String cleanUp(final String codeGen) {
        JSONArray json = new JSONArray();
        json.put(javaParser(codeGen));
        String jsonStr = json.toString();
        return jsonStr.substring(2, jsonStr.length() - 2);
    }

    public static String parse(final String code) {
        gererateContentFile(code);
        generateZnaiDoc();
        return cleanUp(crawler());
    }

    private static String javaParser(String html) {
        html = html.replaceAll("%", "%%");

        boolean isInlineComment = html.contains("code-with-inlined-comments");
        boolean isAnsi = html.contains("cli-output");

        Document doc = Jsoup.parse(html);

        removeComments(doc);

        Document.OutputSettings outputSettings = new Document.OutputSettings();
        outputSettings.prettyPrint(false);
        doc.outputSettings(outputSettings);

        if (isInlineComment) {
            doc = parseInlineComment(doc);
        }

        if (isAnsi) {
            Iterable<Element> ansis = doc.select(".cli-output");
            for (final Element element : ansis) {
                Element pre = element.selectFirst("pre");
                if (pre != null) {
                    pre.addClass("ansi");
                }
            }
        }

        doc.select(".empty-section-title, .snippet-copy-to-clipboard").remove();

        Iterable<Element> elements2 = doc.select(".znai-container-title-wrapper");
        for (final Element element : elements2) {
            String textLabel = element.text();
            Element anchor = element.selectFirst("a");

            if (textLabel != null && anchor == null) {
                String href = "#" + encodeValue(
                        ("title-" + textLabel)
                                .replaceAll("\\s+", "-")
                                .toLowerCase()
                );
                anchor = (new Element("a")).attr("href", href);
            }

            if (anchor != null) {
                anchor.text(textLabel);
                Element divTitle = new Element("div");
                divTitle.attr("id", anchor.attr("href").replace("#", ""));
                divTitle.addClass("title");
                divTitle.appendChild(anchor);
                Element divFragment = new Element("div");
                divFragment.appendText("\n");
                divFragment.appendText("\n");
                divFragment.appendChild(divTitle);
                divFragment.appendText("\n");
                element.replaceWith(divFragment);
                divFragment.unwrap();
            }
        }

        doc.select(".znai-container.content-block")
                .stream()
                .forEach(el -> el.appendChild(new TextNode("\n")));

        doc.select(".strong-emphasis")
                .stream()
                .forEach(el -> el.tagName("strong"));
        // replace div to p tag
        doc.select("div.paragraph.content-block")
                .stream()
                .forEach(el -> el.tagName("p"));
        // clean up attibute class
        doc.select(".znai-inlined-code, .paragraph.content-block, .strong-emphasis")
                .stream()
                .forEach(el -> el.removeAttr("class"));

        Iterable<Element> elements = doc.select(".code-with-inlined-comments, .with-title, .znai-container-title-anchor, .znai-container-title-label, .znai-container.content-block, .snippet, .znai-simple-text");
        for (final Element element : elements) {
            element.unwrap();
        }

        Iterable<Element> pres = doc.select("pre:not(.ansi)");
        for (final Element element : pres) {
            TextNode javaStart = new TextNode("\\n```java\\n");
            TextNode javaEnd = new TextNode("```\\n");
            Element div = new Element("div");
            div.addClass("java");
            div.html(element.outerHtml());
            div.prependChild(javaStart);
            div.appendChild(javaEnd);
            element.replaceWith(div);
        }

        Iterable<Element> codes = doc.select(".java");
        for (final Element element : codes) {
            String rawText = element.text()
                    .replaceAll("\\\\n", "\n")
                    .replaceAll(" \n", "\n")
                    .replaceAll("\n ", "\n");
            element.replaceWith(new DataNode(rawText));
        }

        // attention sign
        Map<String, String> atentionSingMap = new HashMap<>();
        atentionSingMap.put("Note:", "Note");
        atentionSingMap.put("Warning:", "Warning");
        atentionSingMap.put("Question:", "Question");
        atentionSingMap.put("Exercise:", "Exercise");
        atentionSingMap.put("Avoid:", "Avoid");
        atentionSingMap.put("Don't", "Don't");
        atentionSingMap.put("Do not:", "Do notь");
        atentionSingMap.put("Tip:", "Tip");
        atentionSingMap.put("Recommendation:", "Recommendation");
/*      atentionSingMap.put("Note:", "Зверніть увагу");
        atentionSingMap.put("Warning:", "Попередження");
        atentionSingMap.put("Question:", "Запитання");
        atentionSingMap.put("Exercise:", "Вправа");
        atentionSingMap.put("Avoid:", "Уникайте");
        atentionSingMap.put("Don't", "Не робіть");
        atentionSingMap.put("Do not:", "Не робіть");
        atentionSingMap.put("Tip:", "Порада");
        atentionSingMap.put("Recommendation:", "Рекомендація");*/
        
        doc.select(".znai-attention-block-icon")
                .stream()
                .forEach(el -> {
                    Element label = el.selectFirst(".znai-attention-block-label");
                    if (label != null) {
                        String strLabel = label.text();
                        Element icon = el.selectFirst(".znai-icon");
                        if (icon != null) {
                            icon.attr("title", atentionSingMap.get(strLabel));
                        }
                        label.remove();
                    }
                });

        String code = doc.body().html();

        return String.format(
                code.replaceAll("\\\\n", "%n")
                        .replaceAll("http://localhost:3000", "")
        );
    }

    private static Document parseInlineComment(Document doc) {
        Iterable<Element> numElement = doc.select(".znai-tooltip-provider, .znai-circle-badge");
        for (final Element element : numElement) {
            String num = element.text();
            element.text(numberConverter(num));
        }

        return doc;
    }

    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment")) {
                child.remove();
            } else {
                removeComments(child);
                i++;
            }
        }
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static String numberConverter(String num) {
        return "⓿➊➋➌➍➎➏➐➑➒➓⓫⓬⓭⓮⓯⓰⓱⓲⓳⓴".charAt(parseInt(num)) + " ";
    }
}
