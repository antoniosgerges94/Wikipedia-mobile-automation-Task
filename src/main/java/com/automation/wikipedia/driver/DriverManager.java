package com.automation.wikipedia.driver;

import com.automation.wikipedia.config.Config;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public final class DriverManager {
  private static final Logger LOG = Logger.getLogger(DriverManager.class.getName());
  private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();
  private static final ThreadLocal<Integer> SLOT = new ThreadLocal<>();
  private static final AtomicInteger NEXT_SLOT = new AtomicInteger();

  private DriverManager() {}

  public static AppiumDriver driver() {
    AppiumDriver d = DRIVER.get();
    if (d == null)
      throw new IllegalStateException(
          "No Appium session for this scenario. Hooks must create it first.");
    return d;
  }

  public static AppiumDriver driverOrNull() {
    return DRIVER.get();
  }

  public static void createDriver() {
    if (DRIVER.get() != null)
      throw new IllegalStateException("A driver already exists on this thread");
    Config.validate();
    int slot = NEXT_SLOT.getAndIncrement();
    SLOT.set(slot);
    String udid = Config.udids().get(slot % Config.udids().size());
    List<String> urls = Config.appiumUrls();
    String endpoint = urls.get(urls.size() == 1 ? 0 : slot % urls.size());
    try {
      URL url = URI.create(endpoint).toURL();
      AppiumDriver d =
          Config.isAndroid()
              ? new AndroidDriver(url, androidOptions(udid, slot))
              : new IOSDriver(url, iosOptions(udid, slot));
      DRIVER.set(d);
      LOG.info("Session " + d.getSessionId() + " on " + udid + " via " + endpoint);
    } catch (Exception e) {
      SLOT.remove();
      throw new IllegalStateException(
          "Appium session creation failed for " + udid + " via " + endpoint, e);
    }
  }

  public static void quitDriver() {
    AppiumDriver d = DRIVER.get();
    try {
      if (d != null) d.quit();
    } catch (Exception e) {
      LOG.warning("Driver quit failed: " + e.getMessage());
    } finally {
      DRIVER.remove();
      SLOT.remove();
    }
  }

  private static UiAutomator2Options androidOptions(String udid, int slot) {
    UiAutomator2Options o =
        new UiAutomator2Options()
            .setPlatformName("Android")
            .setAutomationName(Config.automationName())
            .setDeviceName(Config.deviceName())
            .setUdid(udid)
            .setAppPackage(Config.appPackage())
            .setAppActivity(Config.appActivity())
            .setNoReset(Config.noReset())
            .setFullReset(Config.fullReset())
            .setAutoGrantPermissions(Config.autoGrantPermissions())
            .setNewCommandTimeout(Config.commandTimeout())
            .setSystemPort(port(Config.systemPorts(), slot, 8200));
    if (!Config.platformVersion().isBlank()) o.setPlatformVersion(Config.platformVersion());
    if (!Config.appWaitActivity().isBlank()) o.setAppWaitActivity(Config.appWaitActivity());
    if (!Config.appPath().isBlank()) o.setApp(Config.appPath());
    return o;
  }

  private static XCUITestOptions iosOptions(String udid, int slot) {
    XCUITestOptions o =
        new XCUITestOptions()
            .setPlatformName("iOS")
            .setAutomationName(Config.automationName())
            .setDeviceName(Config.deviceName())
            .setUdid(udid)
            .setBundleId(Config.bundleId())
            .setWdaLocalPort(port(Config.wdaLocalPorts(), slot, 8100))
            .setNoReset(Config.noReset())
            .setFullReset(Config.fullReset())
            .setAutoAcceptAlerts(Config.autoAcceptAlerts())
            .setNewCommandTimeout(Config.commandTimeout());
    if (!Config.platformVersion().isBlank()) o.setPlatformVersion(Config.platformVersion());
    if (!Config.appPath().isBlank()) o.setApp(Config.appPath());
    return o;
  }

  private static int port(List<String> configured, int slot, int base) {
    if (configured.isEmpty()) return base + slot;
    try {
      return Integer.parseInt(configured.get(slot % configured.size()));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid parallel driver port: " + configured, e);
    }
  }
}
