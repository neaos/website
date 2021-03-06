/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.deservel.website.common.utils;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author DeserveL
 * @date 2017/12/7 17:30
 * @since 1.0.0
 */
public interface HtmlUtils {

    /**
     * markdown转换为html
     *
     * @param markdown
     * @return
     */
    static String mdToHtml(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        String content = renderer.render(document);
        content = EmojiParser.parseToUnicode(content);
        return content;
    }

    /**
     * 替换HTML脚本
     *
     * @param value
     * @return
     */
    static String cleanXSS(String value) {
        //You'll need to remove the spaces from the html entities below
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        return value;
    }

    /**
     * 提取html中的文字
     *
     * @param html
     * @return
     */
    static String htmlToText(String html) {
        if (StringUtils.isNotBlank(html)) {
            return html.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        }
        return "";
    }

    Pattern SRC_PATTERN = Pattern.compile("src\\s*=\\s*\'?\"?(.*?)(\'|\"|>|\\s+)");
    /**
     * 获取文章第一张图片
     *
     * @return
     */
    static String show_thumb(String content) {
        content = mdToHtml(content);
        if (content.contains("<img")) {
            String  img       = "";
            String  regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
            Pattern p_image   = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
            Matcher m_image   = p_image.matcher(content);
            if (m_image.find()) {
                img = img + "," + m_image.group();
                // //匹配src
                Matcher m = SRC_PATTERN.matcher(img);
                if (m.find()) {
                    return m.group(1);
                }
            }
        }
        return "";
    }
}
