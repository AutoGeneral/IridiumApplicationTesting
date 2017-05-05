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
    And I modify the alias "Policy Start Input" by appending it with "')]])[not(@disabled)][last()]"

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

  Scenario: Poicy Start Day
    Given I wait "10" seconds for the page to contain the text "When would you like the policy to start?"
    Then I click the "Policy Start Input" span
    Then I click the "Next" button

  Scenario: Rebuild Cost
    Given I wait "10" seconds for the page to contain the text "What is the total cost to rebuild the home at today's prices?"
    Then I populate the "Rebuild Cost" text box with "Rebuild Cost Input"
    Then I click the "Next" button

  Scenario: Business Activity
    Given I wait "10" seconds for the page to contain the text "Is there any business activity conducted from the home?"
    Then I click the "Business Activity Input" button

  Scenario: Previous Insurance
    Given I wait "10" seconds for the page to contain the text "Have you had home and/or contents insurance in the last 5 years?"
    Then I click the "Had Previous Insurance Input" button

  Scenario: Previous Insurance at Address
    Given I wait "10" seconds for the page to contain the text "Was the previous insurance policy for this address"
    Then I click the "Previous Insurance At Address Input" button

  Scenario: Previous Insurance at Address
    Given I wait "10" seconds for the page to contain the text "Who was your previous insurance with?"
    Then I select "Previous Insurance Input" from the "Previous Insurance" drop down list
    Then I click the "Next" button

  Scenario: Previous Insurance Expiry
    Given I wait "10" seconds for the page to contain the regex "What is the expiry date of your .+? policy\?"
    Then I populate the "Expiry Day" text box with "This Day"
    Then I populate the "Expiry Month" text box with "This Month"
    Then I populate the "Expiry Year" text box with "This Year"
    Then I click the "Next" button

  Scenario: Previous Insurance Length
    Given I wait "10" seconds for the page to contain the regex "How long have you had your .+? policy\?"
    Then I click the "Previous Insurance Length Input" button

  Scenario: Thefts, Burglaries or Claims
    Given I wait "10" seconds for the page to contain the text "Thefts, Burglaries or Claims"
    Then I click the "Thefts Input" button

  Scenario: Date Of Birth
    Given I wait "10" seconds for the page to contain the text "What is the policy holder's date of birth?"
    Then I populate the "DOB Day" text box with "DOB Day Input"
    Then I populate the "DOB Month" text box with "DOB Month Input"
    Then I populate the "DOB Year" text box with "DOB Year Input"
    Then I click the "Next" button

  Scenario: Previous Insurance at Address
    Given I wait "10" seconds for the page to contain the text "Will anyone older than the policy holder live in the home?"
    Then I click the "Older Resident Input" button

  Scenario: Email Address
    Given I wait "10" seconds for the page to contain the text "Email Address"
    Then I click the "Skip" button

  Scenario: Terms Of Use
    Given I wait "10" seconds for the page to contain the text "Terms of Use"
    Then I click the "Yes, I Accept" button

  Scenario: Jump to sales journey
    Then I wait "10" seconds for the page to contain the regex "Your Smart (Home & Contents|Home Only|Contents Only) Insurance Quote"
    And I wait "10" seconds for the "Continue" button to be clickable
    And I click the "Continue" button

  Scenario: Accept duty of disclosure
    Then I wait "10" seconds for the page to contain the text "To finalise your insurance, we need a few more details"
    And I click the "I Agree & Continue" button

  Scenario: Bushfire, storm etc
    Then I wait "10" seconds for the page to contain the text "Is the home under immediate threat of damage by severe storm, bushfire, grassfire or flood?"
    And I click the "Bushfire Input" button

  Scenario: Structurally sound
    Then I wait "10" seconds for the page to contain the text "Is the home structurally sound and watertight?"
    And I click the "Structurally Sound Input" button

  Scenario: Neighbours
    Then I wait "10" seconds for the page to contain the text "Do you have neighbours on all the borders of your property, excluding the street frontage?"
    And I click the "Neighbours Input" button

  Scenario: Neighbours
    Then I wait "10" seconds for the page to contain the text "What borders the property?"
    And I click the "Neighbour One" button
    And I click the "Neighbour Two" button if it exists
    Then I click the "Next" button

  Scenario: Occupied during work hours
    Then I wait "10" seconds for the page to contain the text "Is the home occupied during work hours?"
    And I click the "Occupied Work Hours Input" button

  Scenario: Currently Unoccupied
    Then I wait "10" seconds for the page to contain the text "Is the home currently unoccupied?"
    And I click the "Currently Unoccupied Input" button

  Scenario: Mortgage
    Then I wait "10" seconds for the page to contain the text "Is there a mortgage on the property?"
    And I click the "Mortgage Input" button

  Scenario: Mortgagee
  Note that this screen will not be seen if there is no mortgage on the property
    Then I wait "10" seconds for the page to contain the text "Is there a mortgage on the property?" ignoring timeouts
    Then I select "Mortgagee Input" from the "Mortgage" drop down list if it exists
    Then I click the "Next" button if it exists

  Scenario: Undergoing construction
    Then I wait "10" seconds for the page to contain the text "Is the home under construction or undergoing renovation, alteration or extension?"
    And I click the "Undergoing Construction Input" button

  Scenario: Undergoing construction
    Then I wait "10" seconds for the page to contain the text "Refused or Cancelled Insurance"
    And I click the "Refused Insurance Input" button

  Scenario: Criminal Offence
    Then I wait "10" seconds for the page to contain the text "Have you or any other household member been convicted of a criminal offence?"
    And I click the "Criminal Offence Input" button

  Scenario: Initial Price
    Then I wait "10" seconds for the page to contain the text "Your Price"
    And I sleep for "10" seconds
    And I click the "Refine your cover" button

  Scenario: Motor Burnout
    Then I wait "10" seconds for the page to contain the text "Would you like to add Motor Burnout Cover?"
    And I click the "Motor Burnout Input" button

  Scenario: Accidental Damage
    Then I wait "10" seconds for the page to contain the text "Would you like to add Accidental Damage Cover?"
    And I click the "Accidental Damage Input" button

  Scenario: Flood Cover
    Then I wait "10" seconds for the page to contain the text "Would you like to add Flood Cover?"
    And I click the "Flood Cover Input" button

  Scenario: Final price
    Then I wait "10" seconds for the page to contain the text "Your Price"
    And I sleep for "10" seconds
    And I save the text content of the "Final Price" h2 to the alias "Final Price Value"
    And I modify the alias "Final Price Value" by replacing all characters that match the regex "^\$" with ""
    And I modify the alias "Final Price Value" by dividing "100" into it
    And I dump the value of the alias "Final Price Value" to the console
    And I verify that the alias "Final Price Value" is larger than the alias "Final Price Lower Bound"
    And I verify that the alias "Final Price Value" is smaller than the alias "Final Price Upper Bound"

