Feature: Negative step tests

  @neg-focus-1
  Scenario: Negative dropdown test 1
    Given I open the page "http://google.com"
    Then I focus on the element found by alias "nope"

  @neg-focus-2
  Scenario: Negative dropdown test 2
    Given I open the page "http://google.com"
    Then I focus on the element with the xpath of "nope"