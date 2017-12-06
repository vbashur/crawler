package com.vbashur.adapter;


public class GoogleAdapter implements SearchEngineAdapter {

    public String getSearchEngineUrlPath() {
        return "https://www.google.com/search?q=";
    }

    public String getSearchResultPageRefsPattern() {
        return "#rso h3 a";
    }

}
