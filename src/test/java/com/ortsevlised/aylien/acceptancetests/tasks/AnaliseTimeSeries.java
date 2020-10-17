package com.ortsevlised.aylien.acceptancetests.tasks;

import io.restassured.http.Headers;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import java.util.Map;

import static com.ortsevlised.aylien.acceptancetests.endpoints.AylienEndPoints.*;

public class AnaliseTimeSeries {

    public static Performable forThe(Map<String, String> story) {
        return Task.where("{0} gets the time-series count",
                Get.resource(TimeSeries.path()).with(request -> {
                            request.queryParams(story).and().headers(new Headers(RequestHeaders.authorisation())).log().uri().then().statusCode(200);
                            return request;
                        }
                ));
    }
}