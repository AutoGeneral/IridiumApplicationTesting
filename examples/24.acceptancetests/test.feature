Feature: Fill In Appointment Form
  Scenario: Massage at 6:00
    Given I set the alias mappings
      | First Name | |
      | Last Name  | |
      | Email Address | |
      | Phone Number | |
      | Service Selection List | |
      | Massage | |
      | Appointment Date | |
      | Appointment Time List | |
      | Subscribe To Mailing List | |
      | Comments | |
      | Submit Button | |
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
	Then I verify that the page contains the text "Your booking was successfully submitted"
