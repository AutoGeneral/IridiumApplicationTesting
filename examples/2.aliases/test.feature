Feature: Open an application
  Scenario: Generate Page Object
    Given the alias mappings
      | CQSLaunch             | service_response_vehicle_make     |

  Scenario: Launch App
    And I set the default wait time between steps to "2"
    And I open the application
    And I wait "30" seconds for the element with the ID alias of "CQSLaunch" to be displayed