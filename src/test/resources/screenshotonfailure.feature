Feature: Test of screenshot on failure

  Scenario: Open web page and then fail
    Given I open the page "https://google.com"
    And I fail the scenario
