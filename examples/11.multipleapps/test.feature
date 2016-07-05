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
    And I open the application
    And I wait "30" seconds for the element with the ID alias of "CQSLaunch" to be displayed

  Scenario: Fill in address details
    And I autoselect the post code of alias "dataset-postcode"
    And I populate the element with the ID alias of "CQSStreetSearch" with alias "dataset-address"
    And I wait "30" seconds for the element with the attribute of "key" equal to alias "dataset-addressresult" to be displayed
    And I click an element with an attribute of "key" equal to alias "CQSAddressResult"

  Scenario: Generate page object for launching APP in CQS Sale
    Given the alias mappings
      | CQSPaymentLoaded | service_response_accounts_account0_type |

  Scenario: Reopen quote mid-sale from APP
    And I open the application "app"