Feature: Home Price Test
  Scenario: Setup Aliases
    Given I set the alias mappings
      | Postcode | postcode |
      | Address Capture | address-typeahead |
      | Address Suggestion | //*[@role="option"][1] |
      | Rebuild Cost | //*[@ng-model="$ctrl.amount"] |
      | Previous Insurance | //*[@ng-model="$ctrl.home.previousInsuranceCompanyCode"] |
      | Continue | (.//button[.//*[contains(text(), 'Continue')]])[2] |
      | Expiry Day | tiDay |
      | Expiry Month | tiMonth |
      | Expiry Year | tiYear |
      | DOB Day | tiDay |
      | DOB Month | tiMonth |
      | DOB Year | tiYear |
      | Mortgage | mortgageProvider |
      | Final Price | .price-select-container__price |

  Scenario: Generate some variables
    Given I save the current date with the format "yyyy" to the alias "Year Moved In Input"
    And I modify the alias "Year Moved In Input" by prepending it with "This Year ("
    And I modify the alias "Year Moved In Input" by appending it with ")"
    And I save the current date with the format "MMMM" to the alias "Month Moved In Input"

    And I save the current date with the format "dd" to the alias "Policy Start Input"
    And I modify the alias "Policy Start Input" by prepending it with "(//button[span[contains(., '"
    And I modify the alias "Policy Start Input" by appending it with "')]])[last()]"

    And I save the current date with the format "yyyy" to the alias "This Year"
    And I save the current date with the format "MM" to the alias "This Month"
    And I save the current date with the format "dd" to the alias "This Day"

    And I dump the alias map to the console

  Scenario: Open App
    Given I open the page "https://secure.budgetdirect.com.au/home/2.0/?brand=BUDD"
    And I set the default keystroke delay to "10" milliseconds

  Scenario: Address Capture
    Given I wait "10" seconds for the page to contain the text "What is the address of the property to be insured?"
    Then I populate the "Postcode" textbox with "Postcode Input"
    Then I populate the "Address Capture" textbox with "Address Input"
    Then I click the "Address Suggestion" list item
    Then I click the "Next" button

  Scenario: Property Type
    Given I wait "10" seconds for the page to contain the text "What type of property is the home?"
    Then I click the "Building Type Input" button

  Scenario: Body Corporate
    Given I wait "10" seconds for the page to contain the text "Is the home part of a body corporate/strata title complex?"
    Then I click the "Body Corporate Input" button

  Scenario: Home built
    Given I wait "10" seconds for the page to contain the text "What year was the home built?"
    Then I click the "Year Built Input" button

  Scenario: Wall Type
    Given I wait "10" seconds for the page to contain the text "What is the main construction material for the walls?"
    Then I click the "Wall Type Input" button

  Scenario: Roof Type
    Given I wait "10" seconds for the page to contain the text "What is the main construction material for the roof?"
    Then I click the "Roof Type Input" button

  Scenario: Home Occupied
    Given I wait "10" seconds for the page to contain the text "How is the property occupied?"
    Then I click the "Home Occupied Input" button

  Scenario: Year Moved In
    Given I wait "10" seconds for the page to contain the text "What year did you move in to the property?"
    Then I click the "Year Moved In Input" button

  Scenario: Month moved In
    Given I wait "10" seconds for the page to contain the text "What month in 2017 did you move in to the property?"
    Then I click the "Month Moved In Input" button

  Scenario: Type of quote
    Given I wait "10" seconds for the page to contain the text "What type of cover would you like a quote for?"
    Then I click the "Quote Type Input" button

