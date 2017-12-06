package com.vbashur;


import com.vbashur.adapter.SearchEngineAdapter;
import com.vbashur.client.WebClient;
import com.vbashur.service.WebPageParserService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class JsLibOccurrencesCounter {

    private static final Logger LOG = Logger.getLogger(JsLibOccurrencesCounter.class.getName());

    private SearchEngineAdapter searchEngineAdapter;

    private WebClient webClient;

    private WebPageParserService parserService;


    public JsLibOccurrencesCounter(SearchEngineAdapter adapter, WebClient client, WebPageParserService parser) {
        this.searchEngineAdapter = adapter;
        this.webClient = client;
        this.parserService = parser;
    }

    /**
     * Gets the map of (javascript_library - occurences_number) key-value pairs for the given search key
     *
     * @param searchKey
     * @return
     * @throws IOException
     */
    public Map<String, Long> getJsOccurrencesMap(String searchKey) throws IOException {

        String searchPattern = searchEngineAdapter.getSearchEngineUrlPath() + searchKey;
        Document searchResultPage = this.webClient.getWebDocument(searchPattern);
        LOG.log(Level.FINE, "Getting search result from: " + searchResultPage.title());

        Elements resultRefs = parserService.getSearchResultRefs(searchResultPage, searchEngineAdapter.getSearchResultPageRefsPattern());

        List<Document> webpages = new LinkedList<>();
        for (Element ref : resultRefs) {
            LOG.log(Level.FINE, "Reading WEB page: " + ref.absUrl("href"));
            Document webPage = this.webClient.getWebDocument(ref.absUrl("href"));
            webpages.add(webPage);
        }

        LOG.log(Level.FINE, "Extracting Javascript libraries from WEB pages");
        Map<Document, List<String>> webPagesMap = webpages.parallelStream().collect(Collectors.toMap(p -> p, p -> this.parserService.getDocumentJsLibs(p)));

        LOG.log(Level.FINE, "Calculating Javascript libraries occurrences");
        Map<String, Long> counts = webPagesMap.values().parallelStream()
                .flatMap(jsLibs -> jsLibs.stream())
                .filter(jsLib -> !jsLib.isEmpty())
                .map(this::getJsLibShortName)
                .collect(Collectors.groupingBy(jsLib -> jsLib, counting()));

        return counts;
    }


    /**
     * Extracts the original name of javascript library eg. jquery.min.js -> jquery.js
     *
     * @param fullName
     * @return
     */
    private String getJsLibShortName(String fullName) {
        int firstDotIndex = fullName.indexOf('.');
        int lastDotIndex = fullName.lastIndexOf('.');
        return fullName.substring(0, firstDotIndex) + fullName.substring(lastDotIndex, fullName.length());
    }

}
