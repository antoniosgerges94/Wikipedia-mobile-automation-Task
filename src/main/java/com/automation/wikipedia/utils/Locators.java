package com.automation.wikipedia.utils;

import com.automation.wikipedia.config.Config;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public final class Locators {
  private Locators() {}

  public static By platform(By android, By ios) {
    return Config.isAndroid() ? android : ios;
  }

  public static By androidId(String id) {
    return By.id(id);
  }

  public static By accessibility(String value) {
    return AppiumBy.accessibilityId(value);
  }

  public static By exactText(String value) {
    return Config.isAndroid()
            ? AppiumBy.androidUIAutomator("new UiSelector().text(\"" + ui(value) + "\")")
            : AppiumBy.iOSNsPredicateString(
            "label == '" + predicate(value) + "' OR name == '" + predicate(value) + "'");
  }

  /**
   * Returns nodes that expose text. Callers perform the final exact comparison in Java. This avoids
   * UiSelector.textMatches inconsistencies observed on Android 16 with UiAutomator2 7.6.2.
   */
  public static By textElements() {
    return Config.isAndroid()
            ? By.xpath("//*[@text and string-length(@text)>0]")
            : AppiumBy.iOSNsPredicateString("label != '' OR name != ''");
  }

  /**
   * Locates the current Android Compose result row whose direct TextView child has the requested
   * exact text, ignoring case. The hierarchy is evidenced by the captured Wikipedia search page.
   */
  public static By composeClickableRowByExactText(String value) {
    if (!Config.isAndroid()) {
      return AppiumBy.iOSNsPredicateString(
              "label ==[c] '" + predicate(value) + "' OR name ==[c] '" + predicate(value) + "'");
    }

    String lower = value.toLowerCase(java.util.Locale.ROOT);
    return By.xpath(
            "//android.view.View[@clickable='true']"
                    + "/android.widget.TextView[translate(@text,"
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')="
                    + xpath(lower)
                    + "]/..");
  }

  public static By textContains(String value) {
    return Config.isAndroid()
            ? AppiumBy.androidUIAutomator("new UiSelector().textContains(\"" + ui(value) + "\")")
            : AppiumBy.iOSNsPredicateString(
            "label CONTAINS[c] '"
                    + predicate(value)
                    + "' OR name CONTAINS[c] '"
                    + predicate(value)
                    + "'");
  }

  public static By textOrDescription(String value) {
    return Config.isAndroid()
            ? By.xpath("//*[@text=" + xpath(value) + " or @content-desc=" + xpath(value) + "]")
            : AppiumBy.iOSNsPredicateString(
            "label == '" + predicate(value) + "' OR name == '" + predicate(value) + "'");
  }

  private static String ui(String v) {
    return v.replace("\\", "\\\\").replace("\"", "\\\"");
  }

  private static String predicate(String v) {
    return v.replace("\\", "\\\\").replace("'", "\\'");
  }

  private static String xpath(String v) {
    if (!v.contains("'")) return "'" + v + "'";
    if (!v.contains("\"")) return "\"" + v + "\"";
    String[] p = v.split("'", -1);
    StringBuilder b = new StringBuilder("concat(");
    for (int i = 0; i < p.length; i++) {
      if (i > 0) b.append(",\"'\",");
      b.append("'").append(p[i]).append("'");
    }
    return b.append(')').toString();
  }
}