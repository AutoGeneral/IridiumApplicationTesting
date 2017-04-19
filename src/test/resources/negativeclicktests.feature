Feature: Negative step tests

  @neg-click-1
  Scenario: Negative click test 1
    Given I open the page "http://google.com"
    Then I click the element found by alias "does not exist"

  @neg-click-2
  Scenario: Negative click test 2
    Given I open the page "http://google.com"
    Then I click the element with the ID of "does not exist"

  @neg-click-3
  Scenario: Negative click test 3
    Given I open the page "http://google.com"
    Then I click the hidden element found by alias "does not exist"

  @neg-click-4
  Scenario: Negative click test 4
    Given I open the page "http://google.com"
    Then I click the hidden element with the ID of "does not exist"

  @neg-click-5
  Scenario: Negative click test 5
    Given I open the page "http://google.com"
    Then I click the link with the text content of "does not exist"

  @neg-click-6
  Scenario: Negative click test 6
    Given I open the page "http://google.com"
    Then I click the hidden link with the text content of "does not exist"

  @neg-click-7
  Scenario: Negative click test 7
    Given I open the page "http://google.com"
    Then I click the element with the attribute of "whatever" with a random number between "1" and "10"

  @neg-click-8
  Scenario: Negative click test 8
    Given I open the page "http://google.com"
    Then I click the element with the attribute of "whatever" equal to "does not exist"


  @neg-click-9
  Scenario: Negative click test 11
    Given I open the page "http://google.com"
    Then I "mousedown" "50%" horizontally and "50%" vertically within the area of the element found by "does not exist"

  @neg-click-10
  Scenario: Negative click test 12
    Given I open the page "http://google.com"
    Then I "mousedown" "50%" horizontally and "50%" vertically within the area of the element found by "does not exist"