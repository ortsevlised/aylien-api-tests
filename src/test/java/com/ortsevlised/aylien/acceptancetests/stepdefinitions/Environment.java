package com.ortsevlised.aylien.acceptancetests.stepdefinitions;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;

public class Environment {

    // Automatically injected by Serenity
    private final EnvironmentVariables environmentVariables;

    public Environment(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getRestAPIBaseUrl() {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("restapi.baseurl")
                .orElse("https://api.aylien.com/news/");
    }

    public String getApiAppID() {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("app.id")
                .orElse("some default id");
    }

    public String getApiAppKey() {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("app.key")
                .orElse("some default key");
    }

    public int getXrateHitLimit() {
        return Integer.parseInt(EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("xrate.hit.limit")
                .orElse("60"));
    }

    public String getXrateHitPeriod() {
        return EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("xrate.hit.period")
                .orElse("some default value");
    }
}
