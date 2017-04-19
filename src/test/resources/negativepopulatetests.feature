Feature: Negative step tests

  @neg-populate-1
  Scenario: Negative populate test 1
    Given I open the page "http://google.com"
    Then I populate the element found by "nope" with a random number between alias "10" and alias "20"

  @neg-populate-2
  Scenario: Negative populate test 2
    Given I open the page "http://google.com"
    Then I populate the element found by "nope" with "nope" and submit

  @neg-populate-3
  Scenario: Negative populate test 3
    Given I open the page "http://google.com"
    Then I populate the element found by "nope" with "nope"

  @neg-populate-4
  Scenario: Negative populate test 4
    Given I open the page "http://google.com"
    Then I populate the element with the attribute of "nope" equal to "nope" with alias "nope"

  @neg-populate-5
  Scenario: Negative populate test 5
    Given I open the page "http://google.com"
    Then I populate he element with the css selector of "nope" with a random number between "10" and "20"

  @neg-populate-6
  Scenario: Negative populate test 6
    Given I open the page "http://google.com"
    Then I populate the element with the class of "nope" with "nope" and submit

  @neg-populate-7
  Scenario: Negative populate test 7
    Given I open the page "http://google.com"
    Then I populate the element with the name of "nope" with "nope"

  @neg-populate-8
  Scenario: Negative populate test 8
    Given I open the page "http://google.com"
    Then I populate the hidden element found by alias "nope" with alias "nope"

  @neg-populate-9
  Scenario: Negative populate test 9
    Given I open the page "http://google.com"
    Then I populate the hidden element with the css selector of "nope" with alias "nope"