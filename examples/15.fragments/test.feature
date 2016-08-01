Feature: Open an application

  Scenario: Generate Page Object
    Given the alias mappings
      | Title             | /html/body/center/table/tbody/tr[1]/td/h1     |
      | LoginLink         | [href='login.jsp']                            |
      | RegisterLink      | [href='register.jsp']                         |
      | AboutLink         | [href='about.jsp']                            |
      | BasketLink        | [href='basket.jsp']                           |
      | SearchLink        | [href='search.jsp']                           |
      | SearchInput       | [name='q']                                    |
      | SearchButton      | [value='Search']                              |
      | DoodahsLink       | [href='product.jsp?typeid=6']                 |
      | GizmosLink        | [href='product.jsp?prodid=26']                |
      | GizmosIncrement   | [onclick='incQuantity(26);']                  |
      | ContactLink       | [href='contact.jsp']                          |

  #IMPORT: startzap.fragment

  Scenario: Launch App
    And I set the default wait time between steps to "2"
    And I open the application

  Scenario: Register User
    And I click the element with the css selector alias of "LoginLink"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

    And I click the element with the css selector alias of "RegisterLink"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

    And I populate the element with the ID of "username" with a random number between "1" and "10000000"
    And I populate the element with the ID of "username" with "@a.com"
    And I populate the element with the ID of "password1" with "password"
    And I populate the element with the ID of "password2" with "password"
    And I click the element with the ID of "submit"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

  Scenario: Browse App
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

    And I click the element with the css selector alias of "AboutLink"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

    And I click the element with the css selector alias of "BasketLink"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

    And I click the element with the css selector alias of "SearchLink"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed
    And I populate the element with the css selector alias of "SearchInput" with "doo"
    And I click the element with the css selector alias of "SearchButton"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

  Scenario: Buy Item
    And I click the element with the css selector alias of "DoodahsLink"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

    And I click the element with the css selector alias of "GizmosLink"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

    And I click the element with the ID of "submit"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

  Scenario: Update basket
    And I click the element with the css selector alias of "GizmosIncrement"
    And I click the element with the ID of "update"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

  Scenario: Send Feedback
    And I click the element with the css selector alias of "ContactLink"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed
    And I populate the element with the ID of "comments" with "Some Feedback"
    And I click the element with the ID of "submit"
    And I wait "30" seconds for the element with the xpath alias of "Title" to be displayed

  #IMPORT: finishzap.fragment