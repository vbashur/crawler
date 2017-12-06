package com.vbashur.client;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class WebClient {

    public Document getWebDocument(String webResourceKey) throws IOException {
        return Jsoup.connect(webResourceKey).get();
    }

}
