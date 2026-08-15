package com.automation.wikipedia.pages;

import com.automation.wikipedia.core.BasePage;
import com.automation.wikipedia.utils.Locators;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public final class SearchPage extends BasePage {
  private static final By INPUT = By.id("org.wikipedia:id/search_src_text");
  private static final By EMPTY = By.id("org.wikipedia:id/search_results_empty");

  public void search(String query) {
    requireText(query, "Search query");
    type(INPUT, query);

    if (driver() instanceof AndroidDriver androidDriver) {
      try {
        androidDriver.pressKey(new KeyEvent(AndroidKey.ENTER));
      } catch (Exception ignored) {
        // Wikipedia also updates its results live.
      }
    }

    waitForExactResultRow(query);
  }

  public void openExactResult(String title) {
    requireText(title, "Article title");
    hideKeyboard();

    WebElement row = waitForExactResultRow(title);
    tapCenter(row);

    condition(
            driver -> driver.findElements(INPUT).stream().noneMatch(this::displayed),
            "search screen to close after tapping the exact result row '" + title + "'");
  }

  private WebElement waitForExactResultRow(String title) {
    if (android()) {
      By exactRow = Locators.composeClickableRowByExactText(title);
      final WebElement[] found = {null};

      longCondition(
              driver -> {
                found[0] =
                        driver.findElements(exactRow).stream()
                              .filter(this::displayed)
                              .findFirst()
                              .orElse(null);

                if (found[0] != null) {
                  return true;
                }

                if (driver.findElements(EMPTY).stream().anyMatch(this::displayed)) {
                  throw new AssertionError("Wikipedia returned no result for '" + title + "'");
                }

                return false;
              },
              "evidenced clickable search-result row for '" + title + "'");

      return found[0];
    }

    final WebElement[] found = {null};
    longCondition(
            driver -> {
              found[0] =
                      driver.findElements(Locators.textElements()).stream()
                            .filter(this::displayed)
                            .filter(element -> !inputElement(element))
                            .filter(element -> exact(element, title))
                            .findFirst()
                            .orElse(null);
              return found[0] != null;
            },
            "exact iOS search result '" + title + "'");
    return found[0];
  }

  private static void requireText(String value, String label) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(label + " cannot be blank");
    }
  }
}