package com.ortsevlised.aylien.acceptancetests.tasks;

import io.restassured.http.Headers;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import java.util.Map;

import static com.ortsevlised.aylien.acceptancetests.endpoints.AylienEndPoints.Stories;

public class ViewNewsAbout {

    public static Performable the(Map story) {
        return Task.where("{0} gets news stories}",
                Get.resource(Stories.path()).with(request -> {
                    request.queryParams(story).and().headers(new Headers(RequestHeaders.authorisation())).log().ifValidationFails();
                    return request;
                }));
    }
}
