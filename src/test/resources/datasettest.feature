Feature: A dataset test
  Scenario: Do Google Search
    Given I open the application
    And I populate the element with the name of "q" with alias "Search Term"
    And I click the element found by "Google Search"
