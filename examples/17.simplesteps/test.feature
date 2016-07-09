Feature: Open an application

	# This is where we give readable names to the xpaths, ids, classes, name attributes or
	# css selectors that this test will be interacting with.
	Scenario: Generate Page Object
		Given the alias mappings
			| NoProfileImage 	| //*[@id="ng-app"]/body/div[1]/div/div/div[1]/div/div[2]/div[3]/i 		|
			| ProfileImage 		| //*[@id="ng-app"]/body/div[1]/div/div/div[1]/div/div[2]/div[3]/img 	|
			| LoginBackground 	| ngdialog-overlay			                                      		|

 	# Open up the web page
  	Scenario: Launch App
		And I set the default wait time between steps to "2"
		And I open the application

	# Open the login dialog and close it again
	Scenario: Open Profile
		# Click on an element referencing the aliased xpath we set above
		And I click the element found by alias "NoProfileImage"
	 	# Click on an element referencing the aliased class name we set above
		And I click the element found by alias "LoginBackground"
