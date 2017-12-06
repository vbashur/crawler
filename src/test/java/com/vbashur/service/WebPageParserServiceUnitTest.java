package com.vbashur.service;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WebPageParserServiceUnitTest {


    private Document doc;

    private WebPageParserService parserService;

    @Before
    public void setup() throws IOException {
        File input = new File(getClass().getClassLoader().getResource("regular_page.html").getFile());
        doc = Jsoup.parse(input, "UTF-8", "http://backbonejs.org/");
        parserService = new WebPageParserService();
    }

    @Test
    public void testGetDocumentJsLibs() {
        List<String> resList = parserService.getDocumentJsLibs(doc);
        Assert.assertNotNull(resList);
        Assert.assertEquals(7, resList.size());
        Assert.assertTrue(resList.contains("underscore.js"));
        Assert.assertTrue(resList.contains("jquery.js"));
        Assert.assertTrue(resList.contains("jquery.lazyload.js"));
        Assert.assertTrue(resList.contains("json2.js"));
        Assert.assertTrue(resList.contains("backbone.js"));
        Assert.assertTrue(resList.contains("search.js"));
        Assert.assertTrue(resList.contains("dcmads.js"));
    }
}
