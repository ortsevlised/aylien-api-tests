package com.ortsevlised.aylien.acceptancetests.tasks;

import com.ortsevlised.aylien.helpers.RequestHeaders;
import io.restassured.http.Headers;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import java.util.Map;

import static com.ortsevlised.aylien.acceptancetests.endpoints.AylienEndPoints.*;

public class AnaliseTimeSeries {

    public static Performable forThis(Map<String, String> story) {
        return Task.where("{0} gets the time-series count",
                Get.resource(TimeSeries.path()).with(request ->
                        request.queryParams(story)
                                .with().headers(new Headers(RequestHeaders.authorisation()))
                                .then().statusCode(200)
                                .request().log().uri()
                ));
    }
}