package com.ortsevlised.aylien.acceptancetests.tasks;

import io.restassured.http.Header;

import java.util.ArrayList;
import java.util.List;

public class RequestHeaders {

    public static List<Header> authorisation() {
        List<Header> header = new ArrayList<>();
        header.add(new Header("Content-Type", "application/json"));
        header.add(new Header("X-AYLIEN-NewsAPI-Application-ID", "2c8f6ecc"));
        header.add(new Header("X-AYLIEN-NewsAPI-Application-Key", "63e039e5e61dd0182562725caca1cd73"));
        return header;
    }
}
