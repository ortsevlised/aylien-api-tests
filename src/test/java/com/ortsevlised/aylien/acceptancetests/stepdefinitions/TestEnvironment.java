package com.ortsevlised.aylien.acceptancetests.stepdefinitions;

import net.thucydides.core.util.EnvironmentVariables;

public class TestEnvironment {

    // Automatically injected by Serenity
    private EnvironmentVariables environmentVariables;

    public TestEnvironment(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getRestAPIBaseUrl() {
        return environmentVariables.optionalProperty("restapi.baseurl")
                                   .orElse("https://api.aylien.com/news/");
    }
}
