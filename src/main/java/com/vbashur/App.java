package com.vbashur;

import com.vbashur.adapter.GoogleAdapter;
import com.vbashur.adapter.SearchEngineAdapter;
import com.vbashur.client.WebClient;
import com.vbashur.service.WebPageParserService;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class App {



    private static final Logger LOG = Logger.getLogger(App.class.getName());

    private static final int TOP_LIMIT = 5;

    public static void main(String[] args) {

        if (args == null || args.length != 1) {
            String errorMessage = "Incorrect number of arguments. You must specify a single keyword to search";
            LOG.log(Level.SEVERE, errorMessage);
            return;
        }

        SearchEngineAdapter googleSearchEngineAdapter = new GoogleAdapter();
        WebClient client = new WebClient();
        WebPageParserService parserService = new WebPageParserService();
        try {
            JsLibOccurrencesCounter jsLibOccurrences = new JsLibOccurrencesCounter(googleSearchEngineAdapter, client, parserService);
            Map<String, Long> jsLibOccurrencesMap = jsLibOccurrences.getJsOccurrencesMap(args[0]);

            String result = jsLibOccurrencesMap.entrySet().parallelStream()
                    .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                    .limit(TOP_LIMIT)
                    .map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining("; ", "[", "]"));

            LOG.info("RESULT (format <javascript library name : number of occurrences>) : " + result);
        } catch (IOException ioexcpn) {
            LOG.log(Level.SEVERE, "Unable to get the result. " + ioexcpn.getMessage());
        }
    }


}
