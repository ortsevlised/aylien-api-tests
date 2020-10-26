package com.ortsevlised.aylien.acceptancetests.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Step;

import java.util.Collections;
import java.util.Map;

import static com.ortsevlised.aylien.helpers.StoriesHelper.*;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static org.assertj.core.api.Assertions.assertThat;

public class View implements Task {

    private Map<String, String> queryParams;
    private static boolean allPages;

    public View(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public View() {
    }

    public static View allStories() {
        allPages = true;
        return new View();
    }

    public static View firstPageOfStories() {
        allPages = false;
        return new View();
    }

    public View withThisParameters(Map<String, String> queryParams) {
        return instrumented(View.class, queryParams);
    }

    @Step("{0} views the stories")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(getFirstPageOfStories(queryParams));
        actor.remember(THE_LIST_OF_STORIES, Collections.singletonList(lastResponse().jsonPath()));
        if (allPages) assertThat(allPages(queryParams).size()).isGreaterThan(0);
    }
}