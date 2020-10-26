package com.ortsevlised.aylien.helpers;


import org.hamcrest.Matcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;

public class Matchers {

    //allOf
    //allOf
    //allOf
    //allOf
    //allOf
    //allOf
    //allOf
    //anyOf
    //anyOf
    //anyOf
    //anyOf
    //anyOf
    //anyOf
    //anyOf
    //both
    //either
    //describedAs
    //everyItem
    //isA
    //anything
    //anything
    //hasItem
    //hasItem
    //hasItems
    //hasItems
    //equalTo
    //any
    //instanceOf
    //not
    //not
    //nullValue
    //nullValue
    //notNullValue
    //notNullValue
    //sameInstance
    //theInstance
    //hasItemInArray
    //hasItemInArray
    //arrayContaining
    //arrayContaining
    //arrayContaining
    //arrayContainingInAnyOrder
    //arrayContainingInAnyOrder
    //arrayContainingInAnyOrder
    //arrayWithSize
    //arrayWithSize
    //emptyArray
    //hasSize
    //hasSize
    //emptyCollectionOf
    //emptyIterable
    //emptyIterableOf
    //containsInAnyOrder
    //containsInAnyOrder
    //containsInAnyOrder
    //containsInAnyOrder
    //iterableWithSize
    //iterableWithSize
    //hasEntry
    //hasEntry
    //hasKey
    //hasKey
    //hasValue
    //hasValue
    //isIn
    //isIn
    //isOneOf
    //closeTo
    //closeTo
    //comparesEqualTo
    //greaterThan
    //greaterThanOrEqualTo
    //lessThan
    //lessThanOrEqualTo
    //equalToIgnoringCase
    //equalToIgnoringWhiteSpace
    //isEmptyString
    //isEmptyOrNullString
    //stringContainsInOrder
    //hasToString
    //hasToString
    //typeCompatibleWith
    //eventFrom
    //eventFrom
    //hasProperty
    //hasProperty
    //samePropertyValuesAs
    //hasXPath
    //hasXPath
    //hasXPath
    //hasXPath
    //containsString
    //is
    //is
    //is
    //startsWith
    //endsWith
    //contains
    //contains
    //contains
    //contains
    //array
    //empty
    //wait
    //wait
    //wait
    //equals
    //toString
    //hashCode
    //getClass
    //notify
    //notifyAll
    public static void performAssertion(List<String> check, String actual) {
        try {
            Class<Matcher> matcherClass = (Class<Matcher>) Class.forName("org.hamcrest.Matchers");
            Method[] methods = matcherClass.getMethods();
            String comparator = check.get(1);
            String expected = check.size() == 2 ? "null" : check.get(2);
            HashMap<String, List<Class<?>[]>> hamcrestMethods = new HashMap<>();
            for (Method method : methods) {
                if (hamcrestMethods.get(method.getName()) == null) {
                    List<Class<?>[]> list = new ArrayList<>();
                    list.add(method.getParameterTypes());
                    hamcrestMethods.put(method.getName(), list);
                } else {
                    hamcrestMethods.get(method.getName()).add(method.getParameterTypes());
                }
            }
            Class<?> aClass = check.size() == 2 ? org.hamcrest.Matchers.class : getClass(hamcrestMethods.get(comparator));
            Matcher invoke = (Matcher) matcherClass.getMethod(comparator, aClass).invoke(null, expected);
            assertThat(String.valueOf(check), actual, invoke);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();

            throw new RuntimeException("Something went wrong");

        }
    }

    private static Class<?> getClass(List<Class<?>[]> classes) {
        assertThat(classes, org.hamcrest.Matchers.notNullValue());
        for (Class<?>[] clazz : classes) {
            String name = clazz[0].getName();
            if (name.equals("java.lang.String") || name.equals("java.lang.Object") || name.equals("java.lang.Comparable")) {
                return clazz[0];
            }
        }
        throw new RuntimeException("Something went wrong");
    }
}

