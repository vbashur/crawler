package com.vbashur.service;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebPageParserService {

    /**
     * Extracts the list of references from the first search page result
     * @param searchWebPage
     * @param searchRefsPattern
     * @return
     */
    public Elements getSearchResultRefs(Document searchWebPage, String searchRefsPattern) {
        return searchWebPage.select(searchRefsPattern);
    }

    /**
     * Extracts the list of javascript libraries from the html document
     * @param targetWebPage
     * @return
     */
    public List<String> getDocumentJsLibs(Document targetWebPage) {
        return targetWebPage.getElementsByTag("script").stream()
                .filter(el -> !StringUtil.isBlank(el.attr("src")))
                .map(ref -> extractJsLibName(ref.attr("src")))
                .filter(lib -> !lib.isEmpty())
                .collect(Collectors.toList());
    }

    private String extractJsLibName(String scriptPath) {
        Pattern pattern = Pattern.compile("[^\\/]*\\.js$");
        Matcher matcher = pattern.matcher(scriptPath);
        return matcher.find() ? matcher.group() : "";
    }

}
