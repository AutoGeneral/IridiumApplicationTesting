Feature: Test of screenshot

  Scenario: Open web page and then fail
    Given I open the page "https://google.com"
    Then I take a screenshot called "testscreenshot"
