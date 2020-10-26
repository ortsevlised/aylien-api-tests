package com.ortsevlised.aylien.acceptancetests.tasks;

import io.restassured.path.json.JsonPath;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.thucydides.core.annotations.Step;
import org.jline.utils.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ortsevlised.aylien.helpers.Matchers.performAssertion;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

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
    @Step("{0} validates the response is correct")
    public <T extends Actor> void performAs(T actor) {
        for (String assertion : getAssertions()) {
            do loopThroughResponseAndCheck(listOfChecks(assertion));
            while (!breakingCondition);
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

    /**
     * Loops through the list of checks passed and starts the validation process
     * in each one of them
     *
     * @param listOfChecks a list containing the path, the comparator and the expected value
     *                     for the assertion
     */
    private void loopThroughResponseAndCheck(List<String> listOfChecks) {
        String path = listOfChecks.get(0);
        Object responseObject = response.get(path);
        assertThat(path + " shouldn't be null", responseObject, notNullValue());
        try {
            startValidation(listOfChecks, responseObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Depending on the type of the response object will perform different actions
     *
     * @param listOfChecksToPerform the actions to perform
     * @param responseObject        the response from the request
     * @param <T>                   the type
     */
    private <T> void startValidation(List<String> listOfChecksToPerform, T responseObject) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (responseObject instanceof ArrayList) {
            validateInArray(listOfChecksToPerform, (ArrayList) responseObject);
        } else //if (responseObject instanceof String || responseObject instanceof Integer || responseObject instanceof Boolean || responseObject instanceof HashMap) {
        //TODO check what happens with hashmap
        {
            performAssertion(listOfChecksToPerform, responseObject.toString());
        }
        breakingCondition = true;
    }

    /**
     * Validates the response is not empty and loops through the array till object is
     * instance of String, Integer or Boolean
     *
     * @param listOfChecksToPerform
     * @param responseObject
     */
    private <T> void validateInArray(List<String> listOfChecksToPerform, ArrayList<T> responseObject) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (responseObject.size() == 0) {
            throw new RuntimeException("The response was empty");
        } else {
            for (Object resp : responseObject) {
                performAssertion(listOfChecksToPerform, resp.toString());
                breakingCondition = breakingCondition(responseObject);
            }
        }
    }

    private <T> boolean breakingCondition(ArrayList<T> responseList) {
        return responseList.get(0) instanceof String || responseList.get(0) instanceof Integer || responseList.get(0) instanceof Boolean;
    }

    /**
     * Receives the check to perform
     *
     * @param assertion
     * @return a list with the field to check, the comparator and the expected value
     */
    private List<String> listOfChecks(String assertion) {
        return Arrays.asList(assertion.trim().split(" ", 3));
    }

    /**
     * Separates the string passed by ',' or 'and'
     *
     * @return the assertions to perform
     */
    private String[] getAssertions() {
        return values.split(",|(and)");
    }


}