Feature: A dataset test
  Scenario: Do Google Search
    Given I set the default wait for elements to be available to "5" seconds
    Then I open the page "https://google.com"
    And I populate the element with the name of "q" with alias "Search Term"
    And I click the element found by "Google Search"
