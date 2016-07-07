Feature: Open an application

  Scenario: Generate Page Object
    Given the alias mappings
      | TitleXpath             | /html/body/center/table/tbody/tr[1]/td/h1     |

  Scenario: Initialise ZAP
    Given a scanner with all policies enabled

  Scenario: Launch App
    And I set the default wait time between steps to "2"
    And I open the application
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='about.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='contact.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='login.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='basket.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

    And I click the element with the css selector of "[href='search.jsp']"
    And I wait "30" seconds for the element with the xpath alias of "TitleXpath" to be displayed

  Scenario: Save the results
    And the attack strength is set to "HIGH"
    And the active scanner is run
    And the ZAP XML report is written to the file "zapreport.xml"
    Then no "Medium" or higher risk vulnerabilities should be present for the base url "^https://bodgeit\.herokuapp\.com"