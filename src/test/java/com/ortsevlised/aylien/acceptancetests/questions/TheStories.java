package com.ortsevlised.aylien.acceptancetests.questions;

import com.ortsevlised.aylien.acceptancetests.tasks.ViewNewsAbout;
import io.restassured.path.json.JsonPath;
import net.serenitybdd.rest.SerenityRest;

import java.util.*;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;

public class TheStories {

    public static final String NEXT_PAGE_CURSOR = "next_page_cursor";
    public static final String CURSOR = "cursor";

    /**
     * To be used after the initial request to the stories endpoint.
     * it will send another request for each time the cursor returns a value different
     * than * which indicates there are still stories available.
     * @param story parameter for the GET query
     * @return a list of JsonPath adding each page returned from the API
     */
    public static List<JsonPath> pagination(Map<String, String> story) {
        HashMap<String, String> storyWithNewCursor = new HashMap<>(story); //Story is immutable so I create a new map
        JsonPath lastResponse = SerenityRest.lastResponse().jsonPath();
        List<JsonPath> storyList = new ArrayList<>();
        String cursorToStop = story.get(CURSOR);

        while (!lastResponse.getString(NEXT_PAGE_CURSOR).equals(cursorToStop)) {
            storyList.add(lastResponse);
            String next_page_cursor = lastResponse.get(NEXT_PAGE_CURSOR);
            storyWithNewCursor.put(CURSOR, next_page_cursor);
            theActorInTheSpotlight().attemptsTo(ViewNewsAbout.the(storyWithNewCursor));
            lastResponse = SerenityRest.lastResponse().jsonPath();
        }
        return storyList;
    }
}