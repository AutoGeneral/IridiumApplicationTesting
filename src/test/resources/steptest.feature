Feature: Test of the steps provided by Iridium

	Scenario: Open App
		# Load the page from the appUrlOverride value
		Given I open the application
		# Load the page from the URL
		And I open the page "https://mcasperson.github.io/iridium/examples/test.html"
		And I set the default wait time between steps to "1" seconds
		And I set the default wait for elements to be available to "3" seconds
		And I set the alias mappings
			| Button ID           | buttonId                |
			| Button ID Output    | Button By ID Clicked    |
			| Button Class        | buttonClass             |
			| Button Class Output | Button By Class Clicked |
			| Button Name         | buttonName              |
			| Button Name Output  | Button By Name Clicked  |
			| Button Value        | buttonValue             |
			| Button Value Output | Button By Value Clicked |
			| Button Text         | Button By Text          |
			| Button Text Output  | Button By Text Clicked  |
			| Non-Existant Field  | thisDoesntExist         |
			| Link Contents       | Test Link               |
			| Text Area 1         | textArea                |
			| Text Area 2         | textArea2               |
			| Text Box css		  | body > div:nth-child(3) > input[type="text"]:nth-child(4) |
			| Text Box xpath	  | /html/body/div[2]/input[4] |

	Scenario: Manual Mouse Events
		And I "mousedown" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mousedown"
		And I "mouseup" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mouseup"
		And I "mouseover" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mouseover"
		And I "mouseout" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mouseout"
		And I "mousemove" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mousemove"
		And I "mouseenter" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mouseenter"
		And I "mouseleave" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mouseleave"

	Scenario: Manual Key Events
		And I "keydown" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button keydown"
		And I "keyup" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button keyup"
		And I "keypress" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button keypress"

	Scenario: Work with DropDown Lists
		And I select "Option 2" from the drop down list found by "selectList"
		# This step adds "if it exists" because the Firefox Marionette driver has a bug
		# that will cause this step to fail.
		And I select option number "3" from the drop down list found by "selectList" if it exists

	Scenario: Focus on Elements
		And I focus on the element found by "textArea"
		Then I verify that the page contains the text "Focused on textarea"
		And I focus on the element with the ID of "textArea2"
		Then I verify that the page contains the text "Focused on textarea2"
		And I focus on the element found by alias "Text Area 1"
		Then I verify that the page contains the text "Focused on textarea"
		And I focus on the element with the ID alias of "Text Area 2"
		Then I verify that the page contains the text "Focused on textarea2"
		And I populate the element found by "textArea" with "Some Text"
		And I select all the text in the active element ignoring errors
		And I press the backspace key on the active element "3" times ignoring errors
		And I press the enter key on the active element ignoring errors
		And I populate the element found by "textArea" with "New Line"
		And I press the tab key on the active element ignoring errors

	Scenario: Test Clicking Elements
		And I click the element found by "buttonId"
		Then I verify that the page contains the text "Button By ID Clicked"
		And I click the element found by "buttonClass"
		Then I verify that the page contains the text "Button By Class Clicked"
		And I click the element found by "buttonName"
		Then I verify that the page contains the text "Button By Name Clicked"
		And I click the element found by "buttonValue"
		Then I verify that the page contains the text "Button By Value Clicked"
		And I click the element found by "Button By Text"
		Then I verify that the page contains the text "Button By Text Clicked"
		And I click the element with the ID of "buttonId"
		Then I verify that the page contains the text "Button By ID Clicked"
		And I click the element with the class of "buttonClass"
		Then I verify that the page contains the text "Button By Class Clicked"
		And I click the element with the name of "buttonName"
		Then I verify that the page contains the text "Button By Name Clicked"

	Scenario: Test Clicking Elements With Aliases
		And I click the element found by alias "Button ID"
		Then I verify that the page contains the text alias "Button ID Output"
		And I click the element found by alias "Button Class"
		Then I verify that the page contains the text alias "Button Class Output"
		And I click the element found by alias "Button Name"
		Then I verify that the page contains the text alias "Button Name Output"
		And I click the element found by alias "Button Value"
		Then I verify that the page contains the text alias "Button Value Output"
		And I click the element found by alias "Button Text"
		Then I verify that the page contains the text alias "Button Text Output"
		And I click the element with the ID alias of "Button ID"
		Then I verify that the page contains the text alias "Button ID Output"
		And I click the element with the class alias of "Button Class"
		Then I verify that the page contains the text alias "Button Class Output"
		And I click the element with the name alias of "Button Name"
		Then I verify that the page contains the text alias "Button Name Output"

	Scenario: Test Clicking Hidden Elements
		And I click the hidden element found by "buttonId"
		Then I verify that the page contains the text "Button By ID Clicked"
		And I click the hidden element found by "buttonClass"
		Then I verify that the page contains the text "Button By Class Clicked"
		And I click the hidden element found by "buttonName"
		Then I verify that the page contains the text "Button By Name Clicked"
		And I click the hidden element found by "buttonValue"
		Then I verify that the page contains the text "Button By Value Clicked"
		And I click the hidden element found by "Button By Text"
		Then I verify that the page contains the text "Button By Text Clicked"
		And I click the hidden element with the ID of "buttonId"
		Then I verify that the page contains the text "Button By ID Clicked"
		And I click the hidden element with the class of "buttonClass"
		Then I verify that the page contains the text "Button By Class Clicked"
		And I click the hidden element with the name of "buttonName"
		Then I verify that the page contains the text "Button By Name Clicked"

	Scenario: Test Clicking Hidden Elements With Aliases
		And I click the hidden element found by alias "Button ID"
		Then I verify that the page contains the text alias "Button ID Output"
		And I click the hidden element found by alias "Button Class"
		Then I verify that the page contains the text alias "Button Class Output"
		And I click the hidden element found by alias "Button Name"
		Then I verify that the page contains the text alias "Button Name Output"
		And I click the hidden element found by alias "Button Value"
		Then I verify that the page contains the text alias "Button Value Output"
		And I click the hidden element found by alias "Button Text"
		Then I verify that the page contains the text alias "Button Text Output"
		And I click the hidden element with the ID alias of "Button ID"
		Then I verify that the page contains the text alias "Button ID Output"
		And I click the hidden element with the class alias of "Button Class"
		Then I verify that the page contains the text alias "Button Class Output"
		And I click the hidden element with the name alias of "Button Name"
		Then I verify that the page contains the text alias "Button Name Output"

	Scenario: Test Populating Inputs
		And I populate the element found by "textId" with "Text Box Found By ID"
		And I populate the element found by "textClass" with "Text Box Found By Class" with a keystroke delay of "100" milliseconds
		And I populate the element found by "textName" with "Text Box Found By Name" with a keystroke delay of "50" milliseconds
		And I populate the element found by "textValue" with "Text Box Found By Value" with a keystroke delay of "25" milliseconds
		And I clear the element found by "textId"
		And I clear the element found by "textClass"
		And I clear the element found by "textName"
		And I clear the element with the xpath alias of "Text Box xpath"
		And I populate the element with the ID of "textId" with "Text Box Found By ID"
		And I populate the element with the class of "textClass" with "Text Box Found By Class" with a keystroke delay of "100" milliseconds
		And I populate the element with the name of "textName" with "Text Box Found By Name" with a keystroke delay of "50" milliseconds
		And I populate the element with the css selector alias of "Text Box css" with "Text Box Found By CSS Selector" with a keystroke delay of "25" milliseconds
		And I populate the element with the xpath alias of "Text Box xpath" with " And Then With An XPath" with a keystroke delay of "25" milliseconds
		And I clear the element with the ID of "textId"
		And I clear the element with the class of "textClass"
		And I clear the element with the name of "textName"

	Scenario: Populate Inputs That Are Missing
		And I populate the element found by "thisDoesntExist" with "Whatever" if it exists
		And I populate the element found by alias "Non-Existant Field" with "Whatever" if it exists
		And I populate the element with the ID of "thisDoesntExist" with "Whatver" if it exists
		And I populate the element with the ID alias of "Non-Existant Field" with "Whatver" if it exists

	Scenario: Clicking missing elements
		And I click the element found by "thisDoesntExist" if it exists
		And I click the element found by alias "Non-Existant Field" if it exists
		And I click the element with the ID of "thisDoesntExist" if it exists
		And I click the element with the class of "thisDoesntExist" if it exists
		And I click the element with the name of "thisDoesntExist" if it exists
		And I click the element with the ID alias of "Non-Existant Field" if it exists
		And I click the element with the class alias of "Non-Existant Field" if it exists
		And I click the element with the name alias of "Non-Existant Field" if it exists
		And I click the hidden element found by "thisDoesntExist" if it exists
		And I click the hidden element found by alias "Non-Existant Field" if it exists
		And I click the hidden element with the ID of "thisDoesntExist" if it exists
		And I click the hidden element with the class of "thisDoesntExist" if it exists
		And I click the hidden element with the name of "thisDoesntExist" if it exists
		And I click the hidden element with the ID alias of "Non-Existant Field" if it exists
		And I click the hidden element with the class alias of "Non-Existant Field" if it exists
		And I click the hidden element with the name alias of "Non-Existant Field" if it exists

	Scenario: Verify Element Has Class
		Then I verify that the element found by "verifyDivClass" should have a class of "divClass"
		And I verify that the element with the ID of "verifyDivClass" should have a class of "divClass"
		And I verify that the element found by "#verifyDivClass" should have a class of "divClass"
		And I verify that the element with the css selector of "#verifyDivClass" should have a class of "divClass"
		And I verify that the element found by "divClass" should have a class of "divClass"
		And I verify that the element with the class of "divClass" should have a class of "divClass"
		And I verify that the element found by "//*[@id='verifyDivClass']" should have a class of "divClass"
		And I verify that the element with the xpath of "//*[@id='verifyDivClass']" should have a class of "divClass"
		And I verify that the element found by "A div with a class" should have a class of "divClass"

	Scenario: Verify Page
		And I verify that the browser title should be "Iridium Test Page"

	Scenario: Save Values as Aliases and Verify Them
		And I save the text content of the element found by "verifyString" to the alias "Example String"
		And I save the text content of the element found by "verifyNumber" to the alias "Example Number"
		And I save the text content of the element with the ID of "verifyNumber" to the alias "Example Number"
		And I save the attribute content of "data-verify" from the element found by "verifyNumber" to the alias "Example Number Attr"
		And I save the attribute content of "data-verify" from the element found by "verifyString" to the alias "Example String Attr"
		And I save the value of the element found by "optionValue" to the alias "Option Value"
		Then I verify that the alias "Example Number" is a number
		And I verify that the alias "Example String" is not a number
		And I verify that the alias "Example Number" is not empty
		And I verify that the alias "Example String" is not empty
		And I verify that the alias "Example Number" matches the regex "\d+"
		And I verify that the alias "Example String" matches the regex "[a-zA-Z]+"
		And I verify that the alias "Example Number" does not match the regex "[a-zA-Z]+"
		And I verify that the alias "Example String" does not match the regex "\d+"
		And I verify that the alias "Example Number" is equal to "12345678"
		And I verify that the alias "Example String" is equal to "abcdefg"
		And I verify that the alias "Example Number" is not equal to "This is not a number"
		And I verify that the alias "Example String" is not equal to "This is not the string you are looking for"
		And I verify that the alias "Example Number Attr" is equal to "number"
		And I verify that the alias "Example String Attr" is equal to "string"
		And I verify that the alias "Option Value" is not empty

	Scenario: Save Values from Hidden Elements
		And I save the text content of the hidden element found by "verifyString" to the alias "Example String"
		And I save the text content of the hidden element with the ID of "verifyString" to the alias "Example String"
		And I verify that the alias "Example String" matches the regex "[a-zA-Z]+"

	Scenario: Click Links
		And I click the link with the text content of "Test Link"
		And I click the hidden link with the text content alias of "Link Contents"

	Scenario: Navigate
		And I go back
		And I go forward

	Scenario: Wait steps
		And I wait "30" seconds for the element found by "verifyDivClass" to be displayed
		And I wait "2" seconds for the element found by "thisDoesntExist" to be displayed ignoring timeouts
		And I wait "30" seconds for the element with the ID of "verifyDivClass" to be displayed
		And I wait "2" seconds for the element with the ID alias of "Non-Existant Field" to be displayed ignoring timeouts
		And I sleep for "1" second
		And I wait "30" seconds for the element found by alias "Button ID" to be present
		And I wait "30" seconds for the element found by alias "Button ID" to be clickable
		And I wait "2" seconds for the element found by alias "Non-Existant Field" to be present ignoring timeouts
		And I wait "2" seconds for the element found by alias "Non-Existant Field" to be clickable ignoring timeouts
