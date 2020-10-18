package com.ortsevlised.aylien.acceptancetests.actors;

import com.ortsevlised.aylien.acceptancetests.stepdefinitions.Environment;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.core.util.EnvironmentVariables;

/**
 * The Cast is a factory we use to provide actors for our scenarios.
 * Each actor is given the ability to query our REST API using RestAssured.
 * We assign this cast to a scenario in the SettingTheStage class.
 */
public class CastOfAyliens extends Cast {

    private final Environment environment;

    public CastOfAyliens(EnvironmentVariables environmentVariables) {
        environment = new Environment(environmentVariables);
    }

    @Override
    public Actor actorNamed(String actorName, Ability... abilities) {
        Actor aylien = super.actorNamed(actorName, abilities);
        aylien.can(CallAnApi.at(environment.getRestAPIBaseUrl()));
        return aylien;
    }
}
