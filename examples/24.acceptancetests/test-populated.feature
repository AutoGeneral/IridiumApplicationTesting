Feature: Fill In Appointment Form
  Scenario: Massage at 6:00
    Given I set the alias mappings
      | First Name | firstname |
      | Last Name  | lastname |
      | Email Address | email |
      | Phone Number | phone |
      | Service Selection List | servicelist |
      | Massage | Massage |
      | Appointment Date | calendar-day-2015-09-01 |
      | Appointment Time List | timeList |
      | Subscribe To Mailing List | subscribe |
      | Comments | comments |
      | Submit Button | submit |
      | Success Message | success |
    And I set the default wait time between steps to "2" seconds
    And I open the application
    And I populate the element found by alias "First Name" with "John"
    And I populate the element found by alias "Last Name" with "Smith"
    And I populate the element found by alias "Email Address" with "john@example.org"
    And I populate the element found by alias "Phone Number" with "0411111111"
    And I select alias "Massage" from the drop down list found by alias "Service Selection List"
    And I click the element found by alias "Appointment Date"
    And I select "6:00 PM" from the drop down list found by alias "Appointment Time List"
    And I click the element found by alias "Subscribe To Mailing List"
    And I populate the element found by alias "Comments" with "Can I please get Alice again if she is available?"
    And I click the element found by alias "Submit Button"
    Then I wait "30" seconds for the element found by alias "Success Message" to be displayed
    And I verify that the page contains the text "Your booking was sucessfully submitted"
