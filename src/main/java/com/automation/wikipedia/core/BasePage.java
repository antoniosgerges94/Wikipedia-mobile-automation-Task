package com.automation.wikipedia.core;

import com.automation.wikipedia.config.Config;
import com.automation.wikipedia.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
  protected AppiumDriver driver() {
    return DriverManager.driver();
  }

  protected boolean android() {
    return Config.isAndroid();
  }

  protected WebElement visible(By by) {
    return wait(
            Config.normalWait(), ExpectedConditions.visibilityOfElementLocated(by), "visible " + by);
  }

  protected WebElement clickable(By by) {
    return wait(
            Config.normalWait(), ExpectedConditions.elementToBeClickable(by), "clickable " + by);
  }

  protected List<WebElement> visibleCollection(By by) {
    return wait(
            Config.normalWait(),
            d -> {
              List<WebElement> all = d.findElements(by).stream().filter(this::displayed).toList();
              return all.isEmpty() ? null : all;
            },
            "non-empty visible collection " + by);
  }

  protected WebElement optional(By by) {
    return optional(by, Config.shortWait());
  }

  protected WebElement optionalNormal(By by) {
    return optional(by, Config.normalWait());
  }

  private WebElement optional(By by, Duration timeout) {
    try {
      return rawWait(
              timeout,
              d -> d.findElements(by).stream().filter(this::displayed).findFirst().orElse(null));
    } catch (TimeoutException e) {
      return null;
    }
  }

  protected boolean present(By by) {
    return optional(by) != null;
  }

  protected void gone(By by) {
    wait(
            Config.normalWait(),
            ExpectedConditions.invisibilityOfElementLocated(by),
            "invisible " + by);
  }

  protected void condition(Function<WebDriver, Boolean> condition, String description) {
    wait(Config.normalWait(), condition, description);
  }

  protected void longCondition(Function<WebDriver, Boolean> condition, String description) {
    wait(Config.longWait(), condition, description);
  }

  protected void tap(By by) {
    clickable(by).click();
  }

  protected void tap(WebElement element) {
    wait(Config.normalWait(), d -> enabled(element) ? element : null, "enabled element").click();
  }

  protected void type(By by, String text) {
    WebElement e = clickable(by);
    e.clear();
    e.sendKeys(text);
  }

  protected List<WebElement> find(By by) {
    return driver().findElements(by);
  }

  protected boolean displayed(WebElement e) {
    try {
      return e.isDisplayed();
    } catch (WebDriverException x) {
      return false;
    }
  }

  protected boolean enabled(WebElement e) {
    try {
      return e.isDisplayed() && e.isEnabled();
    } catch (WebDriverException x) {
      return false;
    }
  }

  protected boolean exact(WebElement e, String expected) {
    try {
      return expected.equalsIgnoreCase(e.getText().trim());
    } catch (Exception x) {
      return false;
    }
  }

  protected boolean inputElement(WebElement element) {
    try {
      String tagName = element.getTagName();
      if (tagName == null) {
        return false;
      }
      String normalized = tagName.toLowerCase(java.util.Locale.ROOT);
      return normalized.contains("edittext")
              || normalized.contains("autocompletetextview")
              || normalized.contains("textfield")
              || normalized.contains("searchfield");
    } catch (Exception ignored) {
      return false;
    }
  }

  protected void tapCenter(WebElement e) {
    Rectangle r = e.getRect();
    pointer(
            r.x + r.width / 2,
            r.y + r.height / 2,
            r.x + r.width / 2,
            r.y + r.height / 2,
            Config.tapDuration());
  }

  protected void longPress(WebElement e) {
    Rectangle r = e.getRect();
    pointer(
            r.x + r.width / 2,
            r.y + r.height / 2,
            r.x + r.width / 2,
            r.y + r.height / 2,
            Config.gesture());
  }

  private void pointer(int sx, int sy, int ex, int ey, Duration duration) {
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence s =
            new Sequence(finger, 0)
                    .addAction(
                            finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), sx, sy))
                    .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), ex, ey))
                    .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    driver().perform(List.of(s));
  }

  protected boolean clickAncestor(WebElement element) {
    try {
      WebElement target = clickableAncestor(element);
      wait(Config.normalWait(), d -> enabled(target) ? target : null, "enabled clickable ancestor");
      tapCenter(target);
      return true;
    } catch (Exception primaryFailure) {
      try {
        tapCenter(element);
        return true;
      } catch (Exception fallbackFailure) {
        return false;
      }
    }
  }

  protected void longPressAncestor(WebElement element) {
    WebElement target = clickableAncestor(element);
    wait(Config.normalWait(), d -> enabled(target) ? target : null, "enabled long-click target");
    longPress(target);
  }

  private WebElement clickableAncestor(WebElement element) {
    return element.findElement(By.xpath("./ancestor-or-self::*[@clickable='true'][1]"));
  }

  protected void back() {
    driver().navigate().back();
  }

  protected void hideKeyboard() {
    try {
      driver().executeScript("mobile: hideKeyboard");
    } catch (Exception ignored) {
    }
  }

  private <T> T wait(Duration timeout, Function<WebDriver, T> c, String description) {
    try {
      return rawWait(timeout, c);
    } catch (TimeoutException e) {
      throw new TimeoutException(
              "Timed out after "
                      + timeout
                      + " waiting for "
                      + description
                      + ".\nVisible UI:\n"
                      + screenSummary(),
              e);
    }
  }

  private <T> T rawWait(Duration timeout, Function<WebDriver, T> c) {
    return new WebDriverWait(driver(), timeout)
            .pollingEvery(Config.polling())
            .ignoring(StaleElementReferenceException.class)
            .until(c);
  }

  protected String screenSummary() {
    StringBuilder b = new StringBuilder();
    try {
      for (WebElement e : driver().findElements(By.xpath("//*[@text!='' or @content-desc!='']"))) {
        if (!displayed(e)) continue;
        String t = e.getAttribute("text"), d = e.getAttribute("content-desc");
        if (t != null && !t.isBlank()) b.append("text: ").append(t).append('\n');
        if (d != null && !d.isBlank()) b.append("desc: ").append(d).append('\n');
      }
    } catch (Exception e) {
      b.append("<summary unavailable: ").append(e.getMessage()).append('>');
    }
    return b.isEmpty() ? "<none>" : b.toString();
  }
}