package com.ortsevlised.aylien.acceptancetests.questions;

import com.ortsevlised.aylien.utils.Matchers;
import io.restassured.path.json.JsonPath;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import org.hamcrest.Matcher;
import org.jline.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class Validate implements Interaction {
    private final String values;
    private final JsonPath response;
    private static boolean breakingCondition;

    public Validate(String values, JsonPath response) {
        this.values = values;
        this.response = response;
    }

    public static ValidateResponseBuilder thatThe(String values) {
        return new ValidateResponseBuilder(values);
    }

    /**
     * Check that fields in the fieldValues meet the assertion criteria in the assertions field.
     * Asserts that the field does exist in the map and that it meets the criteria as per assertions.
     * If the user wants to validate that the values for all the elements of the array in the response are the same he should indicate
     * "field.xxx is yyy", if he wants to check an specific element of the array he should indicate like "field[n].xxx is =yyy"
     */

    public <T extends Actor> void performAs(T actor) {
        String[] assertions = values.split(",|(and)");
        for (String assertion : assertions) {
            List<String> listOfChecksToPerform = Arrays.asList(assertion.trim().split(" ", 3));
            do {
                loopThroughResponseAndCheck(listOfChecksToPerform);
            } while (!breakingCondition);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void loopThroughResponseAndCheck(List<String> listOfChecksToPerform) {
        String path = listOfChecksToPerform.get(0);
        Object responseObject = response.get(path);
        assertThat(path + " shouldn't be null", responseObject, notNullValue());
        if (responseObject instanceof ArrayList) {
            ArrayList<Object> responseList = (ArrayList) responseObject;
            if (responseList.size() == 0) {
                throw new RuntimeException("The response was empty");
            } else {
                for (Object resp : responseList) {
                    validation(listOfChecksToPerform, resp);
                    breakingCondition = responseList.get(0) instanceof String || responseList.get(0) instanceof Integer || responseList.get(0) instanceof Boolean;
                }
            }
        } else if (responseObject instanceof String || responseObject instanceof Integer || responseObject instanceof Boolean || responseObject instanceof HashMap) {
            validation(listOfChecksToPerform, responseObject);
            breakingCondition = true;
        }
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private void validation(List<String> check, Object actual) {
        assertThat("The field " + check.get(0) + " is present", actual, notNullValue());
        if (!check.get(2).equalsIgnoreCase("present")) {
            if (check.get(2).equalsIgnoreCase("empty")) {
                check.set(2, "");
            }
            String condition = check.get(0) + " " + check.get(1) + " " + check.get(2);
            Log.debug("Checking that:: " + condition);
            Matcher<String> getMatcher = Matchers.switchMatchers(check.get(1), check.get(2));
            assertThat(condition, actual.toString().trim().toLowerCase(), getMatcher);
        }
    }

    public static class ValidateResponseBuilder {
        private final String values;

        public ValidateResponseBuilder(String values) {
            this.values = values;
        }

        public Validate areInThe(JsonPath response) {
            return instrumented(Validate.class, values, response);
        }
    }
}