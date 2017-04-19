Feature: Negative step tests

  @neg-verify-1
  Scenario: Negative verify test 1
    Given I open the page "http://google.com"
    Then I verify that the element found by "nope" should have a class of "nope"

  @neg-verify-2
  Scenario: Negative verify test 2
    Given I open the page "http://google.com"
    Then I verify that the element with the ID of "nope" has a class of "nope"

  @neg-verify-3
  Scenario: Negative verify test 3
    Given the alias mappings
      | nope 			| 1 |
    Then I verify that the alias "nope" is not a number

  @neg-verify-4
  Scenario: Negative verify test 4
    Given I open the page "http://google.com"
    Then I verify that the page contains the text "nope"

  @neg-verify-5
  Scenario: Negative verify test 5
    Given I open the page "http://google.com"
    Then I verify that the page contains the regex "nope"

  @neg-verify-6
  Scenario: Negative verify test 6
    Given I open the page "http://google.com"
    Then I verify that the page does not contain the text "Google"

  @neg-verify-7
  Scenario: Negative verify test 7
    Given I open the page "http://google.com"
    Then I verify that the page does not contain the regex "Google"

  @neg-verify-8
  Scenario: Negative verify test 8
    Given I open the page "http://google.com"
    Then I verify that the element found by "nope" is displayed

  @neg-verify-9
  Scenario: Negative verify test 9
    Given I open the page "http://google.com"
    Then I verify that the element found by "hplogo" is not displayed

  @neg-verify-10
  Scenario: Negative verify test 10
    Given I open the page "http://google.com"
    Then I verify that the element with the ID of "nope" is displayed

  @neg-verify-11
  Scenario: Negative verify test 11
    Given I open the page "http://google.com"
    Then I verify that the element with the xpath of "//*[contains(text(),'Google Search')]" is not displayed

  @neg-verify-12
  Scenario: Negative verify test 12
    Given I open the page "http://google.com"
    Then I verify that the element found by "nope" is clickable

  @neg-verify-13
  Scenario: Negative verify test 13
    Given I open the page "http://google.com"
    Then I verify that the element with the ID of "nope" is clickable

  @neg-verify-14
  Scenario: Negative verify test 14
    Given I open the page "http://google.com"
    Then I verify that the element found by "nope" is present

  @neg-verify-15
  Scenario: Negative verify test 15
    Given I open the page "http://google.com"
    Then I verify that the element found by "Google Search" is not present

  @neg-verify-16
  Scenario: Negative verify test 16
    Given I open the page "http://google.com"
    Then I verify that the element with the ID of "nope" is present

  @neg-verify-17
  Scenario: Negative verify test 17
    Given I open the page "http://google.com"
    Then I verify that the element with the xpath of "//*[contains(text(),'Google Search')]" is not present

  @neg-verify-18
  Scenario: Negative verify test 18
    Given I open the page "http://google.com"
    Then I verify that a link with the text content of "nope" is present

  @neg-verify-19
  Scenario: Negative verify test 19
    Given I open the page "http://google.com"
    Then I verify that a link with the text content of "Images" is not present

  @neg-verify-20
  Scenario: Negative verify test 20
    Given I open the page "http://google.com"
    Then I verify that the element with the attribute of "nope" equal to "nope" is displayed

  @neg-verify-21
  Scenario: Negative verify test 21
    Given I open the page "http://google.com"
    Then I verify that the element with the attribute of "aria-label" equal to "Google Search" is not displayed

  @neg-verify-22
  Scenario: Negative verify test 22
    Given I open the page "http://google.com"
    Then I verify that the element with the attribute of "nope" equal to "nope" is present

  @neg-verify-23
  Scenario: Negative verify test 23
    Given I open the page "http://google.com"
    Then I verify that the element with the attribute of "id" equal to "hplogo" is not present

  @neg-verify-24
  Scenario: Negative verify test 24
    Given I open the page "http://google.com/nope"
    Then I verify that there were no HTTP errors