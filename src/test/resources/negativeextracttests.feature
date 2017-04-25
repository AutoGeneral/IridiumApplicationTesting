Feature: Negative event tests

  @neg-extract-1
  Scenario: Negative event test 1
    Given I open the page "http://google.com"
    Then I save the attribute content of "whatever" from the element found by "nope" to the alias "whatever"

  @neg-extract-2
  Scenario: Negative event test 2
    Given I open the page "http://google.com"
    Then I save the attribute content of "whatever" from the element with the ID of "nope" to the alias "whatever"

  @neg-extract-3
  Scenario: Negative event test 3
    Given I open the page "http://google.com"
    Then I save the text content of the element found by "nope" to the alias "whatever"

  @neg-extract-4
  Scenario: Negative event test 4
    Given I open the page "http://google.com"
    Then I save the text content of the element with the ID of "nope" to the alias "whatever"

  @neg-extract-5
  Scenario: Negative event test 5
    Given I open the page "http://google.com"
    Then I save the text content of the hidden element found by "nope" to the alias "whatever"

  @neg-extract-6
  Scenario: Negative event test 6
    Given I open the page "http://google.com"
    Then I save the text content of the hidden element with the ID of "nope" to the alias "whatever"

  @neg-extract-7
  Scenario: Negative event test 7
    Given I open the page "http://google.com"
    Then I save the value of the first selected option from the drop down list with the ID of "nope" to the alias "whatever"

  @neg-extract-8
  Scenario: Negative event test 8
    Given I open the page "http://google.com"
    Then I save the value of the first selected option from the drop down list found by "nope" to the alias "whatever"