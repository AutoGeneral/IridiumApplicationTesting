Feature: Open an application
  Scenario: Generate Page Object
    Given the alias mappings
      | CQSLaunch               | service_response_vehicle_make               |
      | CQSStreetSearch         | service_response_address_streetSearch       |
      | CQSAddressStreet        | service_response_address_streetName         |
      | CQSAddressNumber        | service_response_address_streetNum          |
      | CQSVehicleParkedAtNight | service_response_vehicle_parkedAtNight_type |
      | CQSAddressResult        | 7984:Toowong:QLD:248632:55463272:9:12       |

  Scenario: Launch App
    And I set the default wait time between steps to "2"
    And I open the application
    And I wait "30" seconds for the element with the ID alias of "CQSLaunch" to be displayed

  Scenario: Fill in address details
    And I autoselect the post code of "4066"
    And I populate the element with the ID alias of "CQSStreetSearch" with "12/9 Sherwood"
    And I wait "30" seconds for the element with the attribute of "key" equal to alias "CQSAddressResult" to be displayed
    And I click an element with an attribute of "key" equal to alias "CQSAddressResult"