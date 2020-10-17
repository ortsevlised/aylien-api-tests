package com.ortsevlised.aylien.acceptancetests.endpoints;

public enum AylienEndPoints {
    Stories("/stories"),
    TimeSeries("/time_series");

    private final String path;

    AylienEndPoints(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
