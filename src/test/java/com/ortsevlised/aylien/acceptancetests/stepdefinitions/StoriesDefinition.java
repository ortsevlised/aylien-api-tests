package com.ortsevlised.aylien.acceptancetests.stepdefinitions;

import com.aylien.newsapi.JSON;
import com.aylien.newsapi.models.Stories;
import com.ortsevlised.aylien.acceptancetests.tasks.AnaliseTimeSeries;
import com.ortsevlised.aylien.acceptancetests.tasks.Validate;
import com.ortsevlised.aylien.acceptancetests.tasks.View;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import org.jline.utils.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ortsevlised.aylien.acceptancetests.stepdefinitions.SettingTheStage.environment;
import static com.ortsevlised.aylien.helpers.StoriesHelper.*;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.rest.SerenityRest.restAssuredThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class StoriesDefinition {
    private final Actor John = OnStage.theActorCalled("John, the Aylien API user");
    private static Map<String, String> params;
    private final JSON json = new JSON();

    @Given("^(?:.*) retrieves all the stories with the following details:$")
    public void retrieve_stories(DataTable queryParams) {
        params = queryParams.asMaps().get(0); //Using asMaps.get(0) instead of asMap as there's seem to be a bug with the cucumber version
        John.attemptsTo(View.allStories().withThisParameters(params));
    }

    @Then("^(?:.*) should have lots of different stories to read$")
    public void should_see_stories() {
        List<JsonPath> pagesOfStories = John.recall(THE_LIST_OF_STORIES);
        Stories stories = json.deserialize(pagesOfStories.get(0).prettify(), Stories.class);
        assertThat(stories.getStories().size()).isGreaterThan(0);
        assertThat(stories.getStories()).allMatch(
                story -> story.getTitle().toLowerCase().contains(this.params.get("title"))
        );
    }

    @Then("^(?:.*) gets a response where (.*)$")
    public void get_expected_response(String expectedValues) {
        List<JsonPath> pagesOfStories = John.recall(THE_LIST_OF_STORIES);
        assertThat(pagesOfStories.size()).isGreaterThan(0);
        int totalStories = 0;
        for (JsonPath stories : pagesOfStories) {
            John.wasAbleTo(Validate.thatThe(expectedValues).areInThe(stories));
            totalStories += stories.getInt("stories.size()");
        }
        John.remember(AMOUNT_OF_STORIES, totalStories);
    }


    @And("^(?:.*) can see a detailed list with the volume per (.*)$")
    public void see_list_of_stories_in_time(String period) {
        restAssuredThat(response -> {
            response.statusCode(200);
            response.body("period", is(period));
            response.body("time_series.count", is(notNullValue()));
            response.log().body();
        });
    }

    @And("^the amount of stories matches the amount per (.*) from the time series endpoint$")
    public void amount_of_stories_should_match_amount_in_timeseries_endpoitn(String period) {
        John.recall(AMOUNT_OF_STORIES);
        HashMap<String, String> story = new HashMap<>(params); //Story is immutable so I create a new map
        story.put("period", period);
        John.attemptsTo(AnaliseTimeSeries.forThis(story));
    }

    @Then("the selected keyword(s) should appear in either the title or the body")
    public void selected_keyword_appears() {
        List<JsonPath> pagesOfStories = John.recall(THE_LIST_OF_STORIES);
        assertThat(pagesOfStories.size()).isGreaterThan(0);
        pagesOfStories.forEach(page ->
                page.getList("stories").forEach(story ->
                        assertThat(isTheKeywordPresent((HashMap<String, Object>) story)).isTrue()));
    }

    @Given("^(?:.*) retrieves a story$")
    public void retrieve_stories_default() {
        John.attemptsTo(View.firstPageOfStories().withThisParameters(genericStory()));
    }

    @When("^(?:.*) reaches his limit$")
    public void reach_limit_of_hits() {
        Response response = lastResponse();
        assertThat(Integer.parseInt(response.getHeaders().getValue(X_RATELIMIT_HIT_LIMIT))).isEqualTo(environment.getXrateHitLimit());
        assertThat(response.getHeaders().getValue(X_RATELIMIT_HIT_PERIOD)).isEqualTo(environment.getXrateHitPeriod());
        assertThat(hitsRemainingAfterReachingXrateLimit(response)).isEqualTo(0);
        John.attemptsTo(View.firstPageOfStories().withThisParameters(genericStory()));
    }

    @Then("the request is blocked")
    public void theRequestIsBlocked() {
        restAssuredThat(response -> {
            response.statusCode(429);
            response.body("errors.title[0]", is("Too Many Requests"));
        });
    }

    @Given("^(?:.*) wants to see the current volume of stories:$")
    public void compare_volume_of_stories_through_time(DataTable queryParams) {
        params = queryParams.asMaps().get(0); //Using asMaps.get(0) instead of asMap as there's seem to be a bug with the cucumber version
        John.attemptsTo(AnaliseTimeSeries.forThis(params));
    }

    /**
     * Checks that keywords passed on the text param of the request are present either
     * in the title or the body of the story
     * @param story the story got from the request to the story endpoint
     * @return whether the keyword is present
     */
    private boolean isTheKeywordPresent(HashMap<String, Object> story) {
        String storyDetails = story.get("title").toString().concat(story.get("body").toString()).toLowerCase();
        String[] keywords = params.get("text").toLowerCase().split(",");
        Log.info("Keywords to look for:: " + Arrays.toString(keywords));
        return Arrays.stream(keywords).anyMatch(storyDetails::contains);
    }
}


