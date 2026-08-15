@reading-list
Feature: Wikipedia reading-list management

  Background:
    Given the Wikipedia app is launched

  @smoke @data-driven
  Scenario Outline: Save an article in a new reading list and remove it
    When I search for the article "<article>"
    And I open the article from the search results
    Then the article "<article>" should be opened
    When I save the article
    And I add the article to a reading list
    And I create a new reading list named "<readingList>"
    And I navigate to Saved reading-list collections
    And I search for and open the reading list "<readingList>"
    Then the article "<article>" should be visible in the reading list
    When I remove the article "<article>" from the reading list
    Then the article "<article>" should not be visible in the reading list

    Examples:
      | article                 | readingList    |
      | Artificial Intelligence | AI Reading List |

  @duplicate @data-driven
  Scenario Outline: Reject an actual duplicate add to the same reading list
    When I search for the article "<article>"
    And I open the article from the search results
    Then the article "<article>" should be opened
    When I save the article
    And I add the article to a reading list
    And I create a new reading list named "<readingList>"
    And I navigate to Saved reading-list collections
    And I search for and open the reading list "<readingList>"
    Then the article "<article>" should be visible in the reading list
    When I open the article "<article>" from the reading list
    And I attempt to add the article to the existing reading list "<readingList>"
    And I return to the reading list containing "<article>"
    Then the app should confirm the article already exists and show exactly one "<article>" occurrence

    Examples:
      | article                 | readingList    |
      | Artificial Intelligence | AI Reading List |