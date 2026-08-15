/*
package com.automation.wikipedia.pages;

import com.automation.wikipedia.config.Config;
import com.automation.wikipedia.core.BasePage;
import com.automation.wikipedia.utils.Locators;
import java.util.HashSet;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class ReadingListsPage extends BasePage {
  private static final By SAVED_TAB = By.id("org.wikipedia:id/nav_tab_reading_lists");
  private static final By COLLECTIONS = Locators.exactText("Collections");
  private static final By SEARCH_LISTS = By.id("org.wikipedia:id/menu_search_lists");
  private static final By FILTER_MY_LISTS = Locators.accessibility("Filter my lists");
  private static final By SEARCH_INPUT = By.id("org.wikipedia:id/search_src_text");
  private static final By SYNC_READING_LISTS = Locators.exactText("Sync reading lists");
  private static final By NOT_NOW = Locators.exactText("Not now");
  private static final By DISCOVER_PROMOTION =
          Locators.exactText("Discover articles picked just for you");
  private static final By NO_THANKS = Locators.exactText("No thanks");
  private static final By DELETE_SELECTED = By.id("org.wikipedia:id/menu_delete_selected");
  private static final By REMOVE_DIALOG = Locators.exactText("Remove saved articles?");
  private static final By CONFIRM_REMOVE = By.id("android:id/button1");
  private static final By SHARE_COACH_MARK =
          Locators.exactText("Share this reading list with others");
  private static final By GOT_IT = Locators.exactText("Got it");

  public void openSavedCollections() {
    for (int attempt = 0; attempt < Config.maxBackAttempts() && !present(SAVED_TAB); attempt++) {
      back();
    }

    visible(SAVED_TAB);
    tap(SAVED_TAB);

    condition(
            d ->
                    d.findElements(FILTER_MY_LISTS).stream().anyMatch(this::displayed)
                            || d.findElements(SEARCH_LISTS).stream().anyMatch(this::displayed)
                            || d.findElements(COLLECTIONS).stream().anyMatch(this::displayed)
                            || d.findElements(SYNC_READING_LISTS).stream().anyMatch(this::displayed)
                            || d.findElements(DISCOVER_PROMOTION).stream().anyMatch(this::displayed),
            "current Saved screen, Collections tab, or an evidenced Saved prompt");

    if (find(SYNC_READING_LISTS).stream().anyMatch(this::displayed)) {
      tap(NOT_NOW);
      gone(SYNC_READING_LISTS);
    }

    if (find(DISCOVER_PROMOTION).stream().anyMatch(this::displayed)) {
      tap(NO_THANKS);
      gone(DISCOVER_PROMOTION);
    }

    if (find(COLLECTIONS).stream().anyMatch(this::displayed)) {
      tap(COLLECTIONS);
    }

    condition(
            d ->
                    d.findElements(FILTER_MY_LISTS).stream().anyMatch(this::displayed)
                            || d.findElements(SEARCH_LISTS).stream().anyMatch(this::displayed),
            "Saved reading-list filter action");
  }

  public void findAndOpenList(String listName) {
    WebElement filter = optional(FILTER_MY_LISTS);
    if (filter != null) {
      tap(filter);
    } else {
      tap(SEARCH_LISTS);
    }

    visible(SEARCH_INPUT);
    type(SEARCH_INPUT, listName);
    WebElement row = visibleExact(listName);
    if (!clickAncestor(row))
      throw new IllegalStateException("Reading-list row was not actionable: " + listName);
    condition(
            d -> d.findElements(SEARCH_INPUT).stream().noneMatch(this::displayed),
            "reading list navigation for '" + listName + "'");
    dismissShareCoachMarkIfPresent();
  }

  public boolean containsArticle(String title) {
    return count(driver(), title) > 0;
  }

  public int articleCount(String title) {
    return count(driver(), title);
  }

  public void awaitArticle(String title) {
    dismissShareCoachMarkIfPresent();
    condition(d -> count(d, title) > 0, "article '" + title + "' in reading list");
  }

  public void openArticle(String title) {
    WebElement row = visibleArticle(title);
    if (!clickAncestor(row))
      throw new IllegalStateException("Article row was not actionable: " + title);
  }

  public void returnFromArticle(String title) {
    back();
    awaitArticle(title);
  }

  public void removeArticle(String title) {
    WebElement titleElement = visibleArticle(title);
    longPress(titleElement);
    tap(DELETE_SELECTED);
    visible(REMOVE_DIALOG);
    tap(CONFIRM_REMOVE);
    condition(d -> count(d, title) == 0, "article '" + title + "' removed from reading-list UI");
  }

  public void awaitArticleAbsent(String title) {
    condition(d -> count(d, title) == 0, "zero occurrences of article '" + title + "'");
  }

  private void dismissShareCoachMarkIfPresent() {
    WebElement coachMark = optional(SHARE_COACH_MARK);
    if (coachMark != null) {
      tap(GOT_IT);
      gone(SHARE_COACH_MARK);
    }
  }

  private WebElement visibleExact(String text) {
    return visibleCollection(Locators.textElements()).stream()
                                                     .filter(element -> !inputElement(element))
                                                     .filter(element -> exact(element, text))
                                                     .findFirst()
                                                     .orElseThrow(
                                                             () -> new IllegalStateException("Exact non-input text was not visible: " + text));
  }

  private WebElement visibleArticle(String title) {
    return visibleCollection(Locators.textElements()).stream()
                                                     .filter(e -> exact(e, title))
                                                     .findFirst()
                                                     .orElseThrow();
  }

  private int count(WebDriver d, String title) {
    Set<String> bounds = new HashSet<>();
    for (WebElement e : d.findElements(Locators.textElements())) {
      if (displayed(e) && exact(e, title)) bounds.add(e.getRect().toString());
    }
    return bounds.size();
  }
}*/

package com.automation.wikipedia.pages;

import com.automation.wikipedia.config.Config;
import com.automation.wikipedia.core.BasePage;
import com.automation.wikipedia.utils.Locators;
import java.util.HashSet;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class ReadingListsPage extends BasePage {
  private static final By SAVED_TAB = By.id("org.wikipedia:id/nav_tab_reading_lists");
  private static final By COLLECTIONS = Locators.exactText("Collections");
  private static final By SEARCH_LISTS = By.id("org.wikipedia:id/menu_search_lists");
  private static final By FILTER_MY_LISTS = Locators.accessibility("Filter my lists");
  private static final By SEARCH_INPUT = By.id("org.wikipedia:id/search_src_text");
  private static final By SYNC_READING_LISTS = Locators.exactText("Sync reading lists");
  private static final By NOT_NOW = Locators.exactText("Not now");
  private static final By DISCOVER_PROMOTION =
          Locators.exactText("Discover articles picked just for you");
  private static final By NO_THANKS = Locators.exactText("No thanks");
  private static final By REMOVE_ARTICLE_FROM_LIST =
          By.id("org.wikipedia:id/reading_list_item_remove");
  private static final By SHARE_COACH_MARK =
          Locators.exactText("Share this reading list with others");
  private static final By GOT_IT = Locators.exactText("Got it");

  public void openSavedCollections() {
    for (int attempt = 0; attempt < Config.maxBackAttempts() && !present(SAVED_TAB); attempt++) {
      back();
    }

    visible(SAVED_TAB);
    tap(SAVED_TAB);

    condition(
            d ->
                    d.findElements(FILTER_MY_LISTS).stream().anyMatch(this::displayed)
                            || d.findElements(SEARCH_LISTS).stream().anyMatch(this::displayed)
                            || d.findElements(COLLECTIONS).stream().anyMatch(this::displayed)
                            || d.findElements(SYNC_READING_LISTS).stream().anyMatch(this::displayed)
                            || d.findElements(DISCOVER_PROMOTION).stream().anyMatch(this::displayed),
            "current Saved screen, Collections tab, or an evidenced Saved prompt");

    if (find(SYNC_READING_LISTS).stream().anyMatch(this::displayed)) {
      tap(NOT_NOW);
      gone(SYNC_READING_LISTS);
    }

    if (find(DISCOVER_PROMOTION).stream().anyMatch(this::displayed)) {
      tap(NO_THANKS);
      gone(DISCOVER_PROMOTION);
    }

    if (find(COLLECTIONS).stream().anyMatch(this::displayed)) {
      tap(COLLECTIONS);
    }

    condition(
            d ->
                    d.findElements(FILTER_MY_LISTS).stream().anyMatch(this::displayed)
                            || d.findElements(SEARCH_LISTS).stream().anyMatch(this::displayed),
            "Saved reading-list filter action");
  }

  public void findAndOpenList(String listName) {
    WebElement filter = optional(FILTER_MY_LISTS);
    if (filter != null) {
      tap(filter);
    } else {
      tap(SEARCH_LISTS);
    }

    visible(SEARCH_INPUT);
    type(SEARCH_INPUT, listName);
    WebElement row = visibleExact(listName);
    if (!clickAncestor(row))
      throw new IllegalStateException("Reading-list row was not actionable: " + listName);
    condition(
            d -> d.findElements(SEARCH_INPUT).stream().noneMatch(this::displayed),
            "reading list navigation for '" + listName + "'");
    dismissShareCoachMarkIfPresent();
  }

  public boolean containsArticle(String title) {
    return count(driver(), title) > 0;
  }

  public int articleCount(String title) {
    return count(driver(), title);
  }

  public void awaitArticle(String title) {
    dismissShareCoachMarkIfPresent();
    condition(d -> count(d, title) > 0, "article '" + title + "' in reading list");
  }

  public void openArticle(String title) {
    WebElement row = visibleArticle(title);
    if (!clickAncestor(row))
      throw new IllegalStateException("Article row was not actionable: " + title);
  }

  public void returnFromArticle(String title) {
    back();
    awaitArticle(title);
  }

  public void removeArticle(String title) {
    WebElement titleElement = visibleArticle(title);
    longPress(titleElement);
    tap(REMOVE_ARTICLE_FROM_LIST);
    gone(REMOVE_ARTICLE_FROM_LIST);
    condition(d -> count(d, title) == 0, "article '" + title + "' removed from reading-list UI");
  }

  public void awaitArticleAbsent(String title) {
    condition(d -> count(d, title) == 0, "zero occurrences of article '" + title + "'");
  }

  private void dismissShareCoachMarkIfPresent() {
    WebElement coachMark = optional(SHARE_COACH_MARK);
    if (coachMark != null) {
      tap(GOT_IT);
      gone(SHARE_COACH_MARK);
    }
  }

  private WebElement visibleExact(String text) {
    return visibleCollection(Locators.textElements()).stream()
                                                     .filter(element -> !inputElement(element))
                                                     .filter(element -> exact(element, text))
                                                     .findFirst()
                                                     .orElseThrow(
                                                             () -> new IllegalStateException("Exact non-input text was not visible: " + text));
  }

  private WebElement visibleArticle(String title) {
    return visibleCollection(Locators.textElements()).stream()
                                                     .filter(e -> exact(e, title))
                                                     .findFirst()
                                                     .orElseThrow();
  }

  private int count(WebDriver d, String title) {
    Set<String> bounds = new HashSet<>();
    for (WebElement e : d.findElements(Locators.textElements())) {
      if (displayed(e) && exact(e, title)) bounds.add(e.getRect().toString());
    }
    return bounds.size();
  }
}