package com.ortsevlised.aylien.acceptancetests.stepdefinitions;

import com.aylien.newsapi.JSON;
import com.aylien.newsapi.models.Stories;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import com.ortsevlised.aylien.acceptancetests.questions.TheStories;
import com.ortsevlised.aylien.acceptancetests.questions.Validate;
import com.ortsevlised.aylien.acceptancetests.tasks.AnaliseTimeSeries;
import com.ortsevlised.aylien.acceptancetests.tasks.ViewNewsAbout;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import org.jline.utils.Log;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.rest.SerenityRest.restAssuredThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class StoriesDefinition {

    public static final String AMOUNT_OF_STORIES = "amount of stories";
    public static final String X_RATELIMIT_HIT_REMAINING = "x-ratelimit-hit-remaining";
    public static final String X_RATELIMIT_HIT_LIMIT = "x-ratelimit-hit-limit";
    public static final String X_RATELIMIT_HIT_PERIOD = "x-ratelimit-hit-period";
    public static final String MINUTE = "minute";
    public static final int HIT_LIMIT = 60;
    private final Actor John = OnStage.theActorCalled("John, the Aylien API user");
    private final JSON json = new JSON();
    private Map<String, String> stories;

    @Given("^(?:.*) retrieves the stories with the following details:$")
    public void retrieve_stories(DataTable storyParams) {
        stories = storyParams.asMaps().get(0); //I'd use asMap instead of asMaps.get(0), but it seems there's a bug with that.
        John.attemptsTo(ViewNewsAbout.the(stories));
    }

    @Given("^(?:.*) wants to know how the current volume of stories compares to the past:$")
    public void jorgeWantsToKnowHowTheCurrentVolumeOfStoriesComparesToThePast(DataTable queryParams) {
        stories = queryParams.asMaps().get(0);
        John.attemptsTo(AnaliseTimeSeries.forThe(stories));
    }

    @Then("^(?:.*) should have lots of different stories to read$")
    public void should_see_stories() {
        Stories stories = json.deserialize(lastResponse().asString(), Stories.class);
        assertThat(stories.getStories().size()).isGreaterThan(0);
        assertThat(stories.getStories()).allMatch(
                story -> story.getTitle().toLowerCase().contains(this.stories.get("title"))
        );
    }

    @Then("^(?:.*) gets a response where (.*)$")
    public void get_expected_response(String expectedValues) {
        List<JsonPath> stories = TheStories.pagination(this.stories);
        assertThat(stories.size()).isGreaterThan(0);
        int totalStories = 0;
        for (JsonPath story : stories) {
            John.wasAbleTo(Validate.thatThe(expectedValues).areInThe(story));
            totalStories += story.getInt("stories.size()");
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
    public void theAmountOfStoriesMatchesTheAmountFromTheTimeSeriesEndpoint(String period) {
        John.recall(AMOUNT_OF_STORIES);
        HashMap<String, String> story = new HashMap<>(stories); //Story is immutable so I create a new map
        story.put("period", period);
        John.attemptsTo(AnaliseTimeSeries.forThe(story));
    }

    @Then("the selected keyword(s) should appear in either the title or the body")
    public void selected_keyword_appears() {
        List<JsonPath> pagination = TheStories.pagination(stories);
        assertThat(pagination.size()).isGreaterThan(0);
        pagination.forEach(page ->
                page.getList("stories").forEach(story ->
                        assertThat(isTheKeywordPresent((HashMap) story)).isTrue()));
    }

    private boolean isTheKeywordPresent(HashMap<String, Object> story) {
        String storyDetails = story.get("title").toString().concat(story.get("body").toString()).toLowerCase();
        String[] keywords = stories.get("text").trim().toLowerCase().split(",");
        Log.info("Keywords to look for:: " + Arrays.toString(keywords));
        return Arrays.stream(keywords).anyMatch(storyDetails::contains);
    }

    @Given("^(?:.*) retrieves a story$")
    public void retrieve_stories_default() {
        John.attemptsTo(ViewNewsAbout.the(genericStory()));
    }

    @When("^(?:.*) reaches his limit$")
    public void heReachesHisLimit() {
        Response response = lastResponse();
        assertThat(Integer.parseInt(response.getHeaders().getValue(X_RATELIMIT_HIT_LIMIT))).isEqualTo(HIT_LIMIT);
        assertThat(response.getHeaders().getValue(X_RATELIMIT_HIT_PERIOD)).isEqualTo(MINUTE);

        int xRateLimit = Integer.parseInt(response.getHeaders().getValue(X_RATELIMIT_HIT_REMAINING));
        Instant start = Instant.now();
        int count = 0;
        long elapsedTime = 0;
        while (xRateLimit > 0 && elapsedTime < 1) {
            John.attemptsTo(ViewNewsAbout.the(genericStory()));
            xRateLimit = Integer.parseInt(lastResponse().getHeaders().getValue(X_RATELIMIT_HIT_REMAINING));
            Log.info("rate limite" + xRateLimit);
            Instant finish = Instant.now();
            elapsedTime = Duration.between(start, finish).toMinutes();
            count++;
        }
        Log.info("Counter:: " + count);
        if (xRateLimit == 0) {
            John.attemptsTo(ViewNewsAbout.the(genericStory()));
        }
    }

    @Then("the request is blocked")
    public void theRequestIsBlocked() {
        restAssuredThat(response -> {
            response.statusCode(429);
            response.body("errors.title[0]", is("Too Many Requests"));
        });
    }

    private HashMap genericStory() {
        HashMap<String, String> genericStory = new HashMap<>();
        genericStory.put("text", "any");
        genericStory.put("published_at.start", "NOW-1HOUR");
        genericStory.put("per_page", "1");
        return genericStory;
    }
}
