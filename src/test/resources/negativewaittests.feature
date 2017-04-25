Feature: Negative step tests

  @neg-wait-1
  Scenario: Negative wait test 1
    Given I open the page "http://google.com"
    Then I wait "2" seconds for a link with the text content of alias "nope" to be present

  @neg-wait-2
  Scenario: Negative wait test 2
    Given I open the page "http://google.com"
    Then I wait "2" seconds for a link with the text content of "Images" to not be present

  @neg-wait-3
  Scenario: Negative wait test 3
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the page to contain the text "nope"

  @neg-wait-4
  Scenario: Negative wait test 4
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element found by "nope" to be clickable

  @neg-wait-5
  Scenario: Negative wait test 5
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element found by "nope" to be displayed

  @neg-wait-6
  Scenario: Negative wait test 6
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element found by "nope" to be present

  @neg-wait-7
  Scenario: Negative wait test 7
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element found by "hplogo" to not be displayed

  @neg-wait-8
  Scenario: Negative wait test 8
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element found by "hplogo" to not be present

  @neg-wait-9
  Scenario: Negative wait test 9
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the attribute of "nope" equal to "nope" to be displayed

  @neg-wait-10
  Scenario: Negative wait test 10
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the attribute of "nope" equal to "nope" to be present

  @neg-wait-11
  Scenario: Negative wait test 11
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the attribute of "id" equal to "hplogo" to not be displayed

  @neg-wait-12
  Scenario: Negative wait test 12
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the attribute of "id" equal to "hplogo" to not be present

  @neg-wait-13
  Scenario: Negative wait test 13
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the ID of "nope" to be clickable

  @neg-wait-14
  Scenario: Negative wait test 14
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the ID of "nope" to be displayed

  @neg-wait-15
  Scenario: Negative wait test 15
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the ID of "nope" to be present

  @neg-wait-16
  Scenario: Negative wait test 16
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the ID of "hplogo" to not be clickable

  @neg-wait-17
  Scenario: Negative wait test 17
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the ID of "hplogo" to not be displayed

  @neg-wait-18
  Scenario: Negative wait test 18
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the element with the ID of "hplogo" to not be present

  @neg-wait-19
  Scenario: Negative wait test 19
    Given I open the page "http://google.com"
    Then I wait "2" seconds for the page to contain the regex "nope"