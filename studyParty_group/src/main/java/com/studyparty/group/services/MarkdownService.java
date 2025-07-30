package com.studyparty.group.services;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Markdown服务类
 * 处理Markdown转HTML的业务逻辑
 */
@Service
public class MarkdownService {

    // 创建Markdown解析器和渲染器
    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownService() {
        // 配置Markdown扩展
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(), // GFM表格支持
                HeadingAnchorExtension.create() // 标题锚点支持
        );

        // 初始化解析器和渲染器
        this.parser = Parser.builder()
                .extensions(extensions)
                .build();

        this.renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();
    }

    /**
     * 将Markdown文本转换为HTML
     *
     * @param markdown Markdown文本
     * @return HTML文本
     */
    public String renderToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }

        // 解析Markdown
        Node document = parser.parse(markdown);

        // 渲染为HTML
        return renderer.render(document);
    }
}