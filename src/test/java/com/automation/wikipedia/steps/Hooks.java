package com.automation.wikipedia.steps;

import com.automation.wikipedia.driver.DriverManager;
import com.automation.wikipedia.utils.FailureArtifacts;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public final class Hooks {
  @Before(order = 0)
  public void start(Scenario scenario) {
    scenario.log("Creating isolated Appium session");
    DriverManager.createDriver();
  }

  @After(order = 0)
  public void stop(Scenario scenario) {
    try {
      if (scenario.isFailed()) FailureArtifacts.capture(scenario);
    } finally {
      DriverManager.quitDriver();
    }
  }
}
