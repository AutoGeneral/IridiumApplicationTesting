Feature: Open an application
  Scenario: Launch App
    And I set the default wait time between steps to "2"
	And I set header "X-Forwarded-For" with value "127.0.0.1"
	And I remove header "User-Agent"
    And I open the application
    And I verify that there were no HTTP errors
