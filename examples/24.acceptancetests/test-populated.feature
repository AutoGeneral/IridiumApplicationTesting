Feature: Fill In Appointment Form
  Scenario: Massage at 6:00
    Given I set the alias mappings
      | First Name | firstname |
      | Last Name  | lastname |
      | Email Address | email |
      | Phone Number | phone |
      | Service Selection List | servicelist |
      | Appointment Date | calendar-day-2015-09-01 |
      | Appointment Time List | timeList |
      | Subscribe To Mailing List | subscribe |
      | Comments | comments |
      | Submit Button | submit |
    And I open the application
    When I set the default wait time between steps to "0.2" seconds
    And I set the default wait for elements to be available to "60" seconds
    And I populate the element found by alias "First Name" with "John" with a keystroke delay of "25" milliseconds
    And I populate the element found by alias "Last Name" with "Smith" with a keystroke delay of "25" milliseconds
    And I populate the element found by alias "Email Address" with "john@example.org" with a keystroke delay of "25" milliseconds
    And I populate the element found by alias "Phone Number" with "0411111111" with a keystroke delay of "25" milliseconds
    And I select "Massage" from the drop down list found by alias "Service Selection List"
    And I click the element found by alias "Appointment Date"
    And I select "6:00 PM" from the drop down list found by alias "Appointment Time List"
    And I click the element found by alias "Subscribe To Mailing List"
    And I populate the element found by alias "Comments" with "Can I please get Alice again if she is available?" with a keystroke delay of "25" milliseconds
    And I click the element found by alias "Submit Button"
    Then I verify that the page contains the text "Your booking was successfully submitted"
