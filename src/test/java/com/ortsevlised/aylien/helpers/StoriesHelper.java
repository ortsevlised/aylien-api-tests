package com.ortsevlised.aylien.helpers;

import com.ortsevlised.aylien.acceptancetests.tasks.View;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.rest.interactions.Get;
import org.jline.utils.Log;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ortsevlised.aylien.acceptancetests.endpoints.AylienEndPoints.Stories;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

public class StoriesHelper {
    public static final String THE_LIST_OF_STORIES = "The list of stories";
    public static final String NEXT_PAGE_CURSOR = "next_page_cursor";
    public static final String CURSOR = "cursor";
    public static final String X_AYLIEN_NEWS_API_APPLICATION_ID = "X-AYLIEN-NewsAPI-Application-ID";
    public static final String X_AYLIEN_NEWS_API_APPLICATION_KEY = "X-AYLIEN-NewsAPI-Application-Key";
    public static final String X_RATELIMIT_HIT_REMAINING = "x-ratelimit-hit-remaining";
    public static final String X_RATELIMIT_HIT_PERIOD = "x-ratelimit-hit-period";
    public static final String X_RATELIMIT_HIT_LIMIT = "x-ratelimit-hit-limit";
    public static final String AMOUNT_OF_STORIES = "amount of stories";

    public static Performable getFirstPageOfStories(Map<String, String> storyParams) {
        return Task.where("{0} gets news stories",
                Get.resource(Stories.path()).with(request -> {
                    request.queryParams(storyParams).and().headers(new Headers(RequestHeaders.authorisation())).log().uri();
                    return request;
                }));
    }

    /**
     * To be used after the initial request to the stories endpoint.
     * it will send another request for each time the cursor returns a value different
     * than * which indicates there are still stories available.
     *
     * @param params parameter for the GET query
     * @return a list of JsonPath adding each page returned from the API
     */
    public static List<JsonPath> allPages(Map<String, String> params) {
        HashMap<String, String> storyWithNewCursor = new HashMap<>(params); //Story is immutable so I create a new map
        JsonPath lastResponse = lastResponse().jsonPath();
        List<JsonPath> storyList = new ArrayList<>();
        String cursorToStop = params.get(CURSOR);

        while (!lastResponse.getString(NEXT_PAGE_CURSOR).equals(cursorToStop)) {
            storyList.add(lastResponse);
            String next_page_cursor = lastResponse.get(NEXT_PAGE_CURSOR);
            storyWithNewCursor.put(CURSOR, next_page_cursor);
            theActorInTheSpotlight().attemptsTo(getFirstPageOfStories(storyWithNewCursor));
            lastResponse = lastResponse().jsonPath();
        }
        OnStage.theActorInTheSpotlight().remember(THE_LIST_OF_STORIES, storyList);
        return storyList;
    }

    /**
     * Query parameters to use by default
     */
    public static HashMap<String, String> genericStory() {
        HashMap<String, String> genericStory = new HashMap<>();
        genericStory.put("text", "any");
        genericStory.put("published_at.start", "NOW-1HOUR");
        genericStory.put("per_page", "1");
        return genericStory;
    }

    /**
     * Sends Get Request to the Story endpoint till x-ratelimit-hit-remaining is 0 or the time runs out
     *
     * @param response the last response got from the previous request
     * @return the current x-ratelimit-hit-remaining
     */
    public static int hitsRemainingAfterReachingXrateLimit(Response response) {
        int xRateLimit = Integer.parseInt(response.getHeaders().getValue(X_RATELIMIT_HIT_REMAINING));
        Instant start = Instant.now();
        long elapsedTime = 0;

        while (xRateLimit > 0 && elapsedTime < 2) {
            OnStage.theActorInTheSpotlight().attemptsTo(View.firstPageOfStories().withThisParameters(genericStory()));
            xRateLimit = Integer.parseInt(lastResponse().getHeaders().getValue(X_RATELIMIT_HIT_REMAINING));
            Log.info("rate limit" + xRateLimit);
            Instant finish = Instant.now();
            elapsedTime = Duration.between(start, finish).toMinutes();
        }
        return xRateLimit;
    }
}