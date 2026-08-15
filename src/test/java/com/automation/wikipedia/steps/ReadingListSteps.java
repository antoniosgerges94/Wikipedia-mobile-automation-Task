package com.automation.wikipedia.steps;

import com.automation.wikipedia.pages.ArticlePage;
import com.automation.wikipedia.pages.HomePage;
import com.automation.wikipedia.pages.ReadingListsPage;
import com.automation.wikipedia.pages.SearchPage;
import io.cucumber.java.en.*;

public final class ReadingListSteps {
  private final HomePage home = new HomePage();
  private final SearchPage search = new SearchPage();
  private final ArticlePage article = new ArticlePage();
  private final ReadingListsPage lists = new ReadingListsPage();
  private String searchedArticle;
  private boolean duplicateAttemptConfirmedByApp;

  @Given("the Wikipedia app is launched")
  public void launch() {
    home.awaitReady();
  }

  @When("I search for the article {string}")
  public void search(String title) {
    searchedArticle = title;
    home.openSearch();
    search.search(title);
  }

  @When("I open the article from the search results")
  public void openResult() {
    requireArticleContext();
    search.openExactResult(searchedArticle);
  }

  @Then("the article {string} should be opened")
  public void articleOpened(String title) {
    article.awaitArticle(title);
  }

  @When("I save the article")
  public void save() {
    article.saveArticle();
  }

  @When("I add the article to a reading list")
  public void addToReadingList() {
    article.openReadingListPicker();
  }

  @When("I create a new reading list named {string}")
  public void createList(String name) {
    article.createList(name);
  }

  @When("^I navigate to (?:the Reading Lists section|Saved reading-list collections)$")
  public void navigate() {
    lists.openSavedCollections();
  }

  @When("I search for and open the reading list {string}")
  public void openList(String name) {
    lists.findAndOpenList(name);
  }

  @Then("the article {string} should be visible in the reading list")
  public void visible(String title) {
    lists.awaitArticle(title);
    if (!lists.containsArticle(title)) throw new AssertionError("Article is absent: " + title);
  }

  @When("I remove the article {string} from the reading list")
  public void remove(String title) {
    lists.removeArticle(title);
  }

  @Then("the article {string} should not be visible in the reading list")
  public void absent(String title) {
    lists.awaitArticleAbsent(title);
    if (lists.containsArticle(title)) throw new AssertionError("Article remains visible: " + title);
  }

  @When("I open the article {string} from the reading list")
  public void openFromList(String title) {
    lists.openArticle(title);
    article.awaitArticle(title);
  }

  @When("I attempt to add the article to the existing reading list {string}")
  public void duplicateAttempt(String name) {
    duplicateAttemptConfirmedByApp = article.attemptAddToExistingList(name);
  }

  @When("I return to the reading list containing {string}")
  public void returnToList(String title) {
    lists.returnFromArticle(title);
  }

  @Then(
          "the app should confirm the article already exists and show exactly one {string} occurrence")
  public void oneOccurrence(String title) {
    if (!duplicateAttemptConfirmedByApp)
      throw new AssertionError(
              "The AUT did not display its already-exists confirmation after the duplicate attempt");
    int count = lists.articleCount(title);
    if (count != 1)
      throw new AssertionError(
              "Expected one visible occurrence of '" + title + "' but found " + count);
  }

  private void requireArticleContext() {
    if (searchedArticle == null)
      throw new IllegalStateException("No article was searched in this scenario");
  }
}