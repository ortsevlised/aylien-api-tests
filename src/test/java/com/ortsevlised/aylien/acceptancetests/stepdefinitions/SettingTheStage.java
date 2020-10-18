package com.ortsevlised.aylien.acceptancetests.stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import com.ortsevlised.aylien.acceptancetests.actors.CastOfAyliens;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.thucydides.core.util.EnvironmentVariables;

import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;

/**
 * Assign a cast of actors to the scenario stage so that we can call on them in our scenarios.
 * We invoke these actors using the OnStage.actorNamed() and OnStage.theActorInTheSpotlight() methods.
 */
public class SettingTheStage {

    /**
     * The EnvironmentVariables field wraps the system properties and the properties defined in the serenity.properties
     * file. This is a convenient way to access system or environment properties. Serenity will inject it automatically
     * into a step definition class.
     */
    private static EnvironmentVariables environmentVariables;
    public static Environment environment;

    @Before
    public void set_the_stage() {
        setTheStage(new CastOfAyliens(environmentVariables));
        environment = new Environment(environmentVariables);
    }

    @ParameterType(".*")
    public Actor actor(String name) {
        return OnStage.theActorCalled(name);
    }

}
