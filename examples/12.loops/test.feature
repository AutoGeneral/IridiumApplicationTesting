# FeatureGroup: CQS-PROD

Feature: Open an application
  Scenario: Generate Page Object
    Given the alias mappings
      | CQSLaunch               | service_response_vehicle_make               |
      | CQSStreetSearch         | service_response_address_streetSearch       |
      | CQSAddressStreet        | service_response_address_streetName         |
      | CQSAddressNumber        | service_response_address_streetNum          |
      | CQSVehicleParkedAtNight | service_response_vehicle_parkedAtNight_type |

  Scenario: Launch App
    And I set the default wait time between steps to "2"

  Scenario Outline: Repeatedly fill in the address capture field
    And I open the application
    And I wait "30" seconds for the element with the ID alias of "CQSLaunch" to be displayed
    And I autoselect the post code of "4066"
    And I populate the element with the ID alias of "CQSStreetSearch" with "<address>"
    And I wait "30" seconds for the element with the attribute of "key" equal to "<result>" to be displayed
    And I click an element with an attribute of "key" equal to "<result>"

    Examples:
      | address       | result                                |
      | 12/9 Sherwood | 7984:Toowong:QLD:248632:55463272:9:12 |
      | 1 Aston       | 7984:Toowong:QLD:248483:61756081:1:   |