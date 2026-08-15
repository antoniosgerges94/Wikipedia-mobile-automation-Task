package com.automation.wikipedia.utils;

import com.automation.wikipedia.config.Config;
import com.automation.wikipedia.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.Scenario;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.OutputType;

public final class FailureArtifacts {
  private static final DateTimeFormatter FORMAT =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

  private FailureArtifacts() {}

  public static void capture(Scenario scenario) {
    AppiumDriver d = DriverManager.driverOrNull();
    if (d == null) {
      scenario.log("No active driver; UI evidence unavailable");
      return;
    }
    String stem =
        safe(scenario.getName())
            + "-"
            + FORMAT.format(LocalDateTime.now())
            + "-t"
            + Thread.currentThread().threadId();
    try {
      Path dir = Path.of(Config.screenshotsDir());
      Files.createDirectories(dir);
      byte[] png = d.getScreenshotAs(OutputType.BYTES);
      Path file = dir.resolve(stem + ".png");
      Files.write(file, png);
      scenario.attach(png, "image/png", "Failure screenshot");
      scenario.log(file.toAbsolutePath().toString());
    } catch (Exception e) {
      scenario.log("Screenshot capture failed: " + e.getMessage());
    }
    try {
      Path dir = Path.of(Config.screenshotsDir());
      Files.createDirectories(dir);
      byte[] xml = d.getPageSource().getBytes(StandardCharsets.UTF_8);
      Path file = dir.resolve(stem + ".xml");
      Files.write(file, xml);
      scenario.attach(xml, "application/xml", "Failure page source");
      scenario.log(file.toAbsolutePath().toString());
    } catch (Exception e) {
      scenario.log("Page-source capture failed: " + e.getMessage());
    }
  }

  private static String safe(String value) {
    String s = value.replaceAll("[^A-Za-z0-9_-]+", "-").replaceAll("^-|-$", "");
    return s.isBlank() ? "scenario" : s.substring(0, Math.min(70, s.length()));
  }
}
