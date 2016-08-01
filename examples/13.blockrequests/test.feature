Feature: Open an application
  Scenario: Launch App
	And I block access to the URL regex "https://www.google.com" with response "500"
	And I set the default wait time between steps to "2"
	# We are blocking requests to Google, so this will fail
	And I open the application
    And I wait "30" seconds for the element with the attribute of "name" equal to "q" to be displayed
