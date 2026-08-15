package com.automation.wikipedia.pages;

import com.automation.wikipedia.core.BasePage;
import com.automation.wikipedia.utils.Locators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public final class ArticlePage extends BasePage {
  private static final By SEARCH_INPUT = By.id("org.wikipedia:id/search_src_text");
  private static final By SAVE = By.id("org.wikipedia:id/page_save");
  private static final By LANGUAGE = Locators.exactText("Language");
  private static final By FIND_IN_ARTICLE = Locators.exactText("Find in article");
  private static final By CONTENTS = Locators.exactText("Contents");
  private static final By ADD_TO_LIST = Locators.exactText("Add to list");
  private static final By ADD_TO_ANOTHER = Locators.exactText("Add to another reading list");
  private static final By CREATE_NEW = By.id("org.wikipedia:id/create_button");
  private static final By LIST_NAME = By.id("org.wikipedia:id/text_input");
  private static final By OK = By.id("android:id/button1");
  private static final By ALREADY_CONTAINS = Locators.textContains("already contains");
  private static final By GAMES_PROMOTION = Locators.exactText("Wikipedia games");
  private static final By CLOSE_PROMOTION = Locators.accessibility("Close");
  private static final By TOOLBAR_TOOLTIP = Locators.exactText("Customize your toolbar");
  private static final By GOT_IT = Locators.exactText("Got it");

  public void awaitArticle(String title) {
    dismissArticlePrompts();

    longCondition(
            d -> {
              boolean searchGone = d.findElements(SEARCH_INPUT).stream().noneMatch(this::displayed);
              boolean saveActionVisible = d.findElements(SAVE).stream().anyMatch(this::displayed);

              boolean articleToolbarVisible =
                      d.findElements(LANGUAGE).stream().anyMatch(this::displayed)
                              || d.findElements(FIND_IN_ARTICLE).stream().anyMatch(this::displayed)
                              || d.findElements(CONTENTS).stream().anyMatch(this::displayed);
              boolean titleVisible =
                      d.findElements(Locators.textElements()).stream()
                       .filter(this::displayed)
                       .anyMatch(element -> exact(element, title));
              return searchGone && saveActionVisible && articleToolbarVisible && titleVisible;
            },
            "article page structure and title '" + title + "'");
  }

  private void dismissArticlePrompts() {
    for (int attempt = 0; attempt < 4; attempt++) {
      longCondition(
              driver ->
                      driver.findElements(GAMES_PROMOTION).stream().anyMatch(this::displayed)
                              || driver.findElements(TOOLBAR_TOOLTIP).stream().anyMatch(this::displayed)
                              || driver.findElements(SAVE).stream().anyMatch(this::displayed)
                              || driver.findElements(LANGUAGE).stream().anyMatch(this::displayed),
              "article page or an evidenced first-run article prompt");

      if (find(GAMES_PROMOTION).stream().anyMatch(this::displayed)) {
        tap(CLOSE_PROMOTION);
        gone(GAMES_PROMOTION);
        continue;
      }

      if (find(TOOLBAR_TOOLTIP).stream().anyMatch(this::displayed)) {
        tap(GOT_IT);
        gone(TOOLBAR_TOOLTIP);
        continue;
      }

      return;
    }

    throw new IllegalStateException(
            "Article prompts did not clear after the configured bounded attempts.\n" + screenSummary());
  }

  public void saveArticle() {
    tap(SAVE);

    if (optional(ADD_TO_LIST) == null) {
      /*
       * The current build can show a non-accessible toolbar coach mark. A tap outside that popup
       * dismisses it and is consumed. Only when the AUT still shows the unsaved article action and
       * no Add-to-list feedback do we perform one bounded second tap.
       */
      visible(SAVE);
      tap(SAVE);
    }

    visible(ADD_TO_LIST);
  }

  public void openReadingListPicker() {
    tap(ADD_TO_LIST);

    condition(
            driver ->
                    driver.findElements(CREATE_NEW).stream().anyMatch(this::displayed)
                            || driver.findElements(LIST_NAME).stream().anyMatch(this::displayed),
            "reading-list picker or automatically opened create-list dialog");
  }

  public void createList(String listName) {
    requireText(listName);

    if (find(LIST_NAME).stream().noneMatch(this::displayed)) {
      tap(CREATE_NEW);
    }

    visible(LIST_NAME);
    type(LIST_NAME, listName);
    hideKeyboard();
    tap(OK);
    gone(LIST_NAME);
  }

  public boolean attemptAddToExistingList(String listName) {
    requireText(listName);
    tap(SAVE);
    tap(ADD_TO_ANOTHER);
    WebElement list = visibleExact(listName);
    if (!clickAncestor(list))
      throw new IllegalStateException("List row was not actionable: " + listName);
    return optionalNormal(ALREADY_CONTAINS) != null;
  }

  private WebElement visibleExact(String text) {
    return visibleCollection(Locators.textOrDescription(text)).stream()
                                                              .filter(e -> exact(e, text))
                                                              .findFirst()
                                                              .orElseThrow(() -> new IllegalStateException("Exact visible element not found: " + text));
  }

  private static void requireText(String value) {
    if (value == null || value.isBlank())
      throw new IllegalArgumentException("Reading-list name cannot be blank");
  }
}