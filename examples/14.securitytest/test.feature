Feature: Open an application

  Scenario: Generate Page Object
    Given the alias mappings
      | TitleXpath             | /html/body/center/table/tbody/tr[1]/td/h1     |

  Scenario: Initialise ZAP
    Given a scanner with all policies enabled

  Scenario: Launch App
    And I set the default wait time between steps to "2"
    And I open the application

  Scenario: Register User
    And I click the element with the css selector of "[href='login.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='register.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I populate the element with the ID of "username" with a random number between "1" and "10000000"
    And I populate the element with the ID of "username" with "@a.com"
    And I populate the element with the ID of "password1" with "password"
    And I populate the element with the ID of "password2" with "password"
    And I click the element with the ID of "submit"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

  Scenario: Browse App
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='about.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='basket.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='search.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed
    And I populate the element with the css selector of "[name='q']" with "doo"
    And I click the element with the css selector of "[value='Search']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

  Scenario: Buy Item
    And I click the element with the css selector of "[href='product.jsp?typeid=6']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='product.jsp?prodid=26']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the ID of "submit"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

  Scenario: Update basket
    And I click the element with the css selector of "[onclick='incQuantity(26);']"
    And I click the element with the ID of "update"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

  Scenario: Send Feedback
    And I click the element with the css selector of "[href='contact.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed
    And I populate the element with the ID of "comments" with "Some Feedback"
    And I click the element with the ID of "submit"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

  Scenario: Save the results
    And the application is spidered
    And the attack strength is set to "HIGH"
    And the active scanner is run
    And the ZAP XML report is written to the file "zapreport.xml"
    # Ignore X-Frame-Options Header Error
    And the following false positives are ignored
      | url                              | parameter          | cweId      | wascId   |
      | https://bodgeit.herokuapp.com.*  |                    | 16         | 15       |
    Then no "Low" or higher risk vulnerabilities should be present for the base url "^https://bodgeit.herokuapp.com"