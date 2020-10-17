package com.ortsevlised.aylien.utils;


import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.*;

public class Matchers {

    private static final String OR = "<<OR>>";

    public static Matcher<String> switchMatchers(String comparison, String expectedValue) {
        Matcher<String> matcher = null;
        if (expectedValue.equalsIgnoreCase("null")) {
            expectedValue = null;
        }
        switch (comparison) {
            case "is":
            case "=":
                matcher = is(expectedValue);
                break;
            case "not":
                matcher = not(expectedValue);
                break;
            case ">":
                matcher = greaterThan(expectedValue);
                break;
            case "<":
                matcher = lessThan(expectedValue);
                break;
            case "endsWith":
                matcher = endsWith(expectedValue);
                break;
            case "contains":
                matcher = containsString(expectedValue);
                break;
            case "is_any_of":
                String[] values = expectedValue.split(OR);
                matcher = isOneOf(values[0], values[1]);
                break;
            case "doesnt_contain":
                matcher = not(containsString(expectedValue));
                break;
            case "is_empty_or_null":
                matcher = isEmptyOrNullString();
                break;
        }
        return matcher;
    }
}

