package com.wallapp.store;

public interface StaticVars {
    String ALPHA_BASE_URL = "https://source.unsplash.com/featured/";
    String BING_BASE_URL = "https://bing.com";
    String BING_DAILY_URL = BING_BASE_URL + "/HPImageArchive.aspx?format=js&n=7&idx=0";

    String PREF_FIRST_START = "FIRST_START";
    String PREF_SET_AS = "SET_AS";

    String SRC_UNSPLASH = "Unsplash";
    String SRC_BING = "Bing daily";

    String DATE_FORMAT = "yyyyMMdd_HHmmss";

    int HDWidth = 1280;
    int HDHeight = 720;
    int FHDWidth = 1920;
    int FHDHeight = 1080;
    int MAXWidth = 2880;
    int MAXHeight = 1680;

    String APP_NAME = "WallApp";
}
