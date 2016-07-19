Feature: Open an application

	# This is where we give readable names to the xpaths, ids, classes, name attributes or
	# css selectors that this test will be interacting with.
	Scenario: Generate Page Object
		Given the alias mappings
			| HomeLink 			| //*[@id="ng-app"]/body/div[1]/div/div/div[1]/div/div[1]/div/a			|
			| NoProfileImage 	| //*[@id="ng-app"]/body/div[1]/div/div/div[1]/div/div[2]/div[3]/i 		|
			| ProfileImage 		| //*[@id="ng-app"]/body/div[1]/div/div/div[1]/div/div[2]/div[3]/img 	|
			| LoginBackground 	| ngdialog-overlay			                                      		|

 	# Open up the web page
  	Scenario: Launch App
		And I set the default wait time between steps to "2"
		And I open the application
		And I maximise the window

	# Open the login dialog and close it again
	Scenario: Open Profile
		# Click on an element referencing the aliased xpath we set above
		And I click the element found by alias "NoProfileImage"
	 	# Click on an element referencing the aliased class name we set above
		And I click the element found by alias "LoginBackground"

	Scenario: Navigate the main links
		And I click the link with the text content of "REFCARDZ"
		And I click the link with the text content of "GUIDES"
		And I click the link with the text content of "ZONES"
		And I click the link with the text content of "AGILE"
		And I click the link with the text content of "BIG DATA"
		And I click the link with the text content of "CLOUD"
		And I click the link with the text content of "DATABASE"
		And I click the link with the text content of "DEVOPS"
		And I click the link with the text content of "INTEGRATION"
		And I click the link with the text content of "IOT"
		And I click the link with the text content of "JAVA"
		And I click the link with the text content of "MOBILE"
		And I click the link with the text content of "PERFORMANCE"
		And I click the link with the text content of "WEB DEV"

	Scenario: Open some refcardz
		And I click the element found by alias "HomeLink"
		# WebDriver considers this link to be obscured by another element, so
		# we use a special step to click these "hidden" links
		And I click the hidden link with the text content of "Learn Swift"
		And I go back
		And I wait "30" seconds for the element found by alias "HomeLink" to be displayed
		And I click the hidden link with the text content of "Learn Microservices"
		And I go back
		And I wait "30" seconds for the element found by alias "HomeLink" to be displayed
		And I click the hidden link with the text content of "Learn Scrum"
		And I go back
		And I wait "30" seconds for the element found by alias "HomeLink" to be displayed
