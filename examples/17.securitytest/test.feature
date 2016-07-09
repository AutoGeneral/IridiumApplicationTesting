Feature: Open an application

  Scenario: Generate Page Object
    Given the alias mappings
	  | NoProfileImage | //*[@id="ng-app"]/body/div[1]/div/div/div[1]/div/div[2]/div[3]/i |
      | ProfileImage | //*[@id="ng-app"]/body/div[1]/div/div/div[1]/div/div[2]/div[3]/img |

  Scenario: Launch App
    And I set the default wait time between steps to "2"
    And I open the application

  Scenario: Open Profile
	And I click the element found by alias "NoProfileImage"
