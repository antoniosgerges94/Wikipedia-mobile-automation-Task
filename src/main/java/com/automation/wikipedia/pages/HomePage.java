package com.automation.wikipedia.pages;

import com.automation.wikipedia.config.Config;
import com.automation.wikipedia.core.BasePage;
import com.automation.wikipedia.utils.Locators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public final class HomePage extends BasePage {
  private static final By HOME = By.id("org.wikipedia:id/nav_tab_home");
  private static final By SEARCH = By.id("org.wikipedia:id/nav_tab_search");
  private static final By SEARCH_CARD = By.id("org.wikipedia:id/search_card");
  private static final By SEARCH_INPUT = By.id("org.wikipedia:id/search_src_text");
  private static final By SEARCH_WIDGET_PROMOTION = Locators.exactText("A Faster way to Search");
  private static final By CLOSE_SEARCH_WIDGET_PROMOTION = Locators.accessibility("Close");
  private static final By ONBOARDING_ACTION =
          By.xpath(
                  "//*[@content-desc='Forward' or @text='Skip' or @text='Get started' or @text='Continue'"
                          + " or @text='Next' or @text='Done']");

  public void awaitReady() {
    for (int i = 0; i < Config.maxOnboardingActions() && !mainNavigation(); i++) {
      WebElement action = optional(ONBOARDING_ACTION);
      if (action == null) break;
      if (!clickAncestor(action))
        throw new IllegalStateException(
                "Onboarding action was present but not actionable.\n" + screenSummary());
    }
    condition(d -> mainNavigation(), "Wikipedia main navigation after onboarding");
    if (present(HOME)) tap(HOME);
  }

  public void openSearch() {
    tap(SEARCH);

    condition(
            d ->
                    d.findElements(SEARCH_INPUT).stream().anyMatch(this::displayed)
                            || d.findElements(SEARCH_CARD).stream().anyMatch(this::displayed)
                            || d.findElements(SEARCH_WIDGET_PROMOTION).stream().anyMatch(this::displayed),
            "Wikipedia search screen or its first-run promotion");

    dismissSearchWidgetPromotion();

    if (present(SEARCH_INPUT)) {
      return;
    }

    tap(SEARCH_CARD);
    visible(SEARCH_INPUT);
  }

  private void dismissSearchWidgetPromotion() {
    if (find(SEARCH_WIDGET_PROMOTION).stream().anyMatch(this::displayed)) {
      tap(CLOSE_SEARCH_WIDGET_PROMOTION);
      gone(SEARCH_WIDGET_PROMOTION);
    }
  }

  private boolean mainNavigation() {
    return find(HOME).stream().anyMatch(this::displayed)
            || find(SEARCH).stream().anyMatch(this::displayed);
  }
}