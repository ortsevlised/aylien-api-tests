package com.ortsevlised.aylien.helpers;

import io.restassured.http.Header;

import java.util.ArrayList;
import java.util.List;

import static com.ortsevlised.aylien.acceptancetests.stepdefinitions.SettingTheStage.environment;
import static com.ortsevlised.aylien.helpers.StoriesHelper.X_AYLIEN_NEWS_API_APPLICATION_ID;
import static com.ortsevlised.aylien.helpers.StoriesHelper.X_AYLIEN_NEWS_API_APPLICATION_KEY;

public class RequestHeaders {

    public static List<Header> authorisation() {
        List<Header> header = new ArrayList<>();
        header.add(new Header("Content-Type", "application/json"));
        header.add(new Header(X_AYLIEN_NEWS_API_APPLICATION_ID, environment.getApiAppID()));
        header.add(new Header(X_AYLIEN_NEWS_API_APPLICATION_KEY, environment.getApiAppKey()));
        return header;
    }
}
