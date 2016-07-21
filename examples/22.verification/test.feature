Feature: Buy Concert Tickets on TicketMonster
  Scenario: Set alias mappings
    When I set the alias mappings
      | Venue Selection Drop Down List | venueSelector |
      | Order tickets Button | bookButton |
      | Section Selection Drop Down List | sectionSelect |
      | Adult Ticket Prices | #ticket-category-fieldset-5 > div > div > span |
      | Adult Ticket Count | tickets-1 |
      | Add tickets Button | add |
      | Email | email |
      | Checkout | submit |
      | First Ticket Number | //*[@id="content"]/div[2]/div[2]/div/table/tbody/tr[1]/td[1] |
      | Second Ticket Number | //*[@id="content"]/div[2]/div[2]/div/table/tbody/tr[2]/td[1] |

  Scenario: Open Application
    When I set the default wait time between steps to "2" seconds
    And I open the application
    And I wait "10" seconds for the element found by "logo" to be displayed

  Scenario: Open Events Page
    And I click the link with the text content of "Events"
    And I click the link with the text content of "Concert"
    And I click the link with the text content of "Rock concert of the decade"

  Scenario: Order tickets
    And I select "Sydney : Sydney Opera House" from the drop down list found by alias "Venue Selection Drop Down List"
    And I click the element found by alias "Order tickets Button"

  Scenario: Checkout
    And I select "S1 - Front left" from the drop down list found by alias "Section Selection Drop Down List"
    And I save the text content of the element found by alias "Adult Ticket Prices" to the alias "Adult Ticket Prices Value"
    And I verify that the alias "Adult Ticket Prices Value" matches the regex "@ \$\d+\.\d{2}"
    And I populate the element found by alias "Adult Ticket Count" with "2"
    And I click the element found by alias "Add tickets Button"
    And I populate the element found by alias "Email" with "a@a.com"
    And I click the element found by alias "Checkout"

  Scenario: Verify Checkout
    And I save the text content of the element found by alias "First Ticket Number" to the alias "First Ticket Number Value"
    And I save the text content of the element found by alias "Second Ticket Number" to the alias "Second Ticket Number Value"
    Then I verify that the alias "First Ticket Number Value" is a number
    Then I verify that the alias "Second Ticket Number Value" is a number
