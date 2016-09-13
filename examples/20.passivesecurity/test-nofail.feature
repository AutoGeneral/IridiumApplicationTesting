Feature: Open an application

	# This is where we give readable names to the xpaths, ids, classes, name attributes or
	# css selectors that this test will be interacting with.
	Scenario: Generate Page Object
		Given the alias mappings
			| HomeLink 			| //*[@id="ng-app"]/body/div[1]/div/div/div[1]/div/div[1]/div/a			|

 	# Open up the web page
  	Scenario: Launch App
		Given a scanner with all policies enabled
        And I set the default wait time between steps to "0.2"
		And I set the default wait for elements to be available to "60" seconds
        # Allow all traffic to the main domain
		And I enable the whitelist with responding with "500" for unmatched requests
		And I allow access to the URL regex ".*?dzone.*"
	    # Speed up tests by blocking thumbnails
		And I block access to the URL regex ".*?thumbnail.*" with response "500"
		And I open the application
		And I maximise the window

	Scenario: Navigate some of the main links
		And I click the link with the text content of "REFCARDZ"
		And I click the link with the text content of "GUIDES"
		And I click the link with the text content of "ZONES"

	Scenario: Save the results
		And the application is spidered timing out after "15" seconds
		And the ZAP XML report is written to the file "zapreport.xml"
		Then I report any "Low" or higher risk vulnerabilities for the base url "^https://dzone.com"
