Feature: Negative event tests

  @neg-event-1
  Scenario: Negative event test 1
    Given I open the page "http://google.com"
    Then I dispatch a "mousedown" event on the hidden element found by "this doesn't exist"

  @neg-event-2
  Scenario: Negative event test 2
    Given I open the page "http://google.com"
    Then I dispatch a "mousedown" event on the hidden element with the name of "this doesn't exist"