Feature: Negative step tests

  @neg-dropdown-1
  Scenario: Negative dropdown test 1
    Given I open the page "http://google.com"
    Then I select "nope" from the drop down list found by "nope"

  @neg-dropdown-2
  Scenario: Negative dropdown test 2
    Given I open the page "http://google.com"
    Then I select "nope" from the drop down list with the ID of "nope"

  @neg-dropdown-3
  Scenario: Negative dropdown test 3
    Given I open the page "http://google.com"
    Then I select option number "1" from the drop down list found by "nope"

  @neg-dropdown-4
  Scenario: Negative dropdown test 4
    Given I open the page "http://google.com"
    Then I select option number "1" from the drop down list with the ID of "nope"
