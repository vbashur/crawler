package com.vbashur;


import com.vbashur.adapter.GoogleAdapter;
import com.vbashur.adapter.SearchEngineAdapter;
import com.vbashur.client.WebClient;
import com.vbashur.service.WebPageParserService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.fail;

public class JsLibOccurrencesCounterUnitTest {


    @Test
    public void testTwoValidPages() {
        try {

            String page1Reference = UUID.randomUUID().toString();
            String page2Reference = UUID.randomUUID().toString();

            Document webPage1 = new Document(page1Reference);
            Document webPage2 = new Document(page2Reference);

            WebClient webClient = Mockito.mock(WebClient.class);

            Mockito.when(webClient.getWebDocument(Mockito.anyString())).thenReturn(webPage1);
            Mockito.when(webClient.getWebDocument(page1Reference)).thenReturn(webPage1);
            Mockito.when(webClient.getWebDocument(page2Reference)).thenReturn(webPage2);
            List<String> expectedPage1JsLibs = Arrays.asList("underscore.js", "jquery.js", "search.js");
            List<String> expectedPage2JsLibs = Arrays.asList("underscore.js", "jquery.js", "backbone.js");

            Element stubElement1 = Mockito.mock(Element.class);
            Mockito.when(stubElement1.absUrl("href")).thenReturn(page1Reference);
            Element stubElement2 = Mockito.mock(Element.class);
            Mockito.when(stubElement2.absUrl("href")).thenReturn(page2Reference);
            Elements stubResultRefs = new Elements();
            stubResultRefs.add(stubElement1);
            stubResultRefs.add(stubElement2);

            WebPageParserService parser = Mockito.mock(WebPageParserService.class);
            Mockito.when(parser.getSearchResultRefs(Mockito.anyObject(), Mockito.anyString())).thenReturn(stubResultRefs);
            Mockito.when(parser.getDocumentJsLibs(webPage1)).thenReturn(expectedPage1JsLibs);
            Mockito.when(parser.getDocumentJsLibs(webPage2)).thenReturn(expectedPage2JsLibs);

            JsLibOccurrencesCounter jsLibOccurrences = new JsLibOccurrencesCounter(new GoogleAdapter(), webClient, parser);
            Map<String, Long> resMap = jsLibOccurrences.getJsOccurrencesMap(UUID.randomUUID().toString());
            Assert.assertNotNull(resMap);
            Assert.assertEquals(Long.valueOf(2), resMap.get("underscore.js"));
            Assert.assertEquals(Long.valueOf(2), resMap.get("jquery.js"));
            Assert.assertEquals(Long.valueOf(1), resMap.get("search.js"));
            Assert.assertEquals(Long.valueOf(1), resMap.get("backbone.js"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnavailablePage() {
        try {
            SearchEngineAdapter adapter = Mockito.mock(GoogleAdapter.class);
            Mockito.when(adapter.getSearchEngineUrlPath()).thenReturn(UUID.randomUUID().toString());
            JsLibOccurrencesCounter jsLibOccurrences = new JsLibOccurrencesCounter(adapter, new WebClient(), new WebPageParserService());
            jsLibOccurrences.getJsOccurrencesMap(UUID.randomUUID().toString());
            fail("Exception is expected");
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }


}
