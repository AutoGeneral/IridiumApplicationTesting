Feature: Open an application
  Scenario: Launch App
    And I set the default wait time between steps to "2"
    And I open the application
    And I wait "30" seconds for the element with the ID of "service_response_vehicle_make" to be displayed