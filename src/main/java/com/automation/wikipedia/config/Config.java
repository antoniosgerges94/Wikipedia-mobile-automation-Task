package com.automation.wikipedia.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public final class Config {
  private static final Properties VALUES = load();

  private Config() {}

  private static Properties load() {
    Properties values = new Properties();
    try (InputStream in =
        Config.class.getClassLoader().getResourceAsStream("config/config.properties")) {
      if (in == null) throw new IllegalStateException("Missing config/config.properties");
      values.load(in);
      return values;
    } catch (IOException e) {
      throw new IllegalStateException("Cannot load config/config.properties", e);
    }
  }

  public static String get(String key, String fallback) {
    String system = System.getProperty(key);
    if (system != null && !system.isBlank()) return system.trim();
    String environment = System.getenv(key.toUpperCase(Locale.ROOT).replace('.', '_'));
    if (environment != null && !environment.isBlank()) return environment.trim();
    String file = VALUES.getProperty(key);
    return file == null || file.isBlank() ? fallback : file.trim();
  }

  public static String get(String key) {
    return get(key, "");
  }

  public static boolean bool(String key, boolean fallback) {
    String v = get(key);
    return v.isBlank() ? fallback : Boolean.parseBoolean(v);
  }

  public static long number(String key, long fallback) {
    String v = get(key);
    try {
      return v.isBlank() ? fallback : Long.parseLong(v);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(key + " must be numeric: " + v, e);
    }
  }

  public static List<String> list(String key) {
    String v = get(key);
    return v.isBlank()
        ? List.of()
        : Arrays.stream(v.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
  }

  public static String platformName() {
    return get("platform.name", "Android");
  }

  public static boolean isAndroid() {
    return platformName().equalsIgnoreCase("Android");
  }

  public static boolean isIos() {
    return platformName().equalsIgnoreCase("iOS");
  }

  public static List<String> appiumUrls() {
    List<String> v = list("appium.url");
    return v.isEmpty() ? List.of("http://127.0.0.1:4723") : v;
  }

  public static List<String> udids() {
    return list("udid");
  }

  public static List<String> systemPorts() {
    return list("system.port");
  }

  public static List<String> wdaLocalPorts() {
    return list("wda.local.port");
  }

  public static String deviceName() {
    return get("device.name", isAndroid() ? "Android Emulator" : "iPhone Simulator");
  }

  public static String platformVersion() {
    return get("platform.version");
  }

  public static String automationName() {
    return get("automation.name", isAndroid() ? "UiAutomator2" : "XCUITest");
  }

  public static String appPackage() {
    return get("app.package", "org.wikipedia");
  }

  public static String appActivity() {
    return get("app.activity", "org.wikipedia.main.MainActivity");
  }

  public static String appWaitActivity() {
    return get("app.wait.activity");
  }

  public static String appPath() {
    return get("app.path");
  }

  public static String bundleId() {
    return get("bundle.id", "org.wikimedia.wikipedia");
  }

  public static boolean noReset() {
    return bool("no.reset", false);
  }

  public static boolean fullReset() {
    return bool("full.reset", false);
  }

  public static boolean autoGrantPermissions() {
    return bool("auto.grant.permissions", true);
  }

  public static boolean autoAcceptAlerts() {
    return bool("auto.accept.alerts", true);
  }

  public static Duration commandTimeout() {
    return Duration.ofSeconds(number("new.command.timeout", 300));
  }

  public static Duration normalWait() {
    return Duration.ofSeconds(number("wait.timeout.seconds", 20));
  }

  public static Duration shortWait() {
    return Duration.ofSeconds(number("wait.short.timeout.seconds", 3));
  }

  public static Duration longWait() {
    return Duration.ofSeconds(number("wait.long.timeout.seconds", 40));
  }

  public static Duration polling() {
    return Duration.ofMillis(number("wait.polling.millis", 250));
  }

  public static Duration gesture() {
    return Duration.ofMillis(number("gesture.duration.millis", 650));
  }

  public static Duration tapDuration() {
    return Duration.ofMillis(number("gesture.tap.millis", 100));
  }

  public static int maxOnboardingActions() {
    return Math.toIntExact(number("onboarding.max.actions", 8));
  }

  public static int maxBackAttempts() {
    return Math.toIntExact(number("navigation.max.back.attempts", 6));
  }

  public static String screenshotsDir() {
    return get("screenshots.dir", "target/failure-screenshots");
  }

  public static void validate() {
    if (!isAndroid() && !isIos())
      throw new IllegalArgumentException("platform.name must be Android or iOS");
    if (appiumUrls().size() != 1 && appiumUrls().size() != udids().size())
      throw new IllegalArgumentException("Configure one Appium URL or one URL per UDID");
    if (isAndroid() && udids().isEmpty())
      throw new IllegalArgumentException("Android requires udid");
    if (normalWait().isZero() || shortWait().isZero() || longWait().isZero() || polling().isZero())
      throw new IllegalArgumentException("Wait values must be positive");
  }
}
