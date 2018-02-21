Feature: Test of the steps provided by Iridium

	@test
	Scenario: Open App
		Given I display a starting marker
	  # Load the page from the appUrlOverride value
		And I open the application
	  # Load the page from the URL
		And I open the page "https://mcasperson.github.io/iridium/examples/test.html"
		Then I verify that there were no HTTP errors
		And I set the default wait time between steps to "0.2" seconds
		And I set the default wait for elements to be available to "3" seconds
		And I set the alias mappings
			| Button ID            | buttonId                                                  |
			| Button ID Output     | Button By ID Clicked                                      |
			| Button Class         | buttonClass                                               |
			| Button Class Output  | Button By Class Clicked                                   |
			| Button Name          | buttonName                                                |
			| Button Name Output   | Button By Name Clicked                                    |
			| Button Value         | buttonValue                                               |
			| Button Value Output  | Button By Value Clicked                                   |
			| Button Text          | Button By Text                                            |
			| Button Text Output   | Button By Text Clicked                                    |
			| Non-Existant Field   | thisDoesntExist                                           |
			| Link Contents        | Test Link                                                 |
			| Text Area 1          | textArea                                                  |
			| Text Area 2          | textArea2                                                 |
			| Text Box css         | body > div:nth-child(3) > input[type="text"]:nth-child(4) |
			| Text Box xpath       | /html/body/div[2]/input[4]                                |
			| Test Value 1         | 100                                                       |
			| Test Value 2         | 200.15                                                    |
			| Test Value 3         | 100                                                       |
			| Test Value 4         | $1,234.50                                                 |
			| Test Value 6         | Test Value                                                |
			| Test Value 7         | Another Value                                             |
			| Test Value 8         | A test value                                              |
			| Test Value 9         | A test value                                              |
			| Test Value 10        | 123.45                                                    |
			| Event Button         | eventButton                                               |
			| MouseDown Text       | Button mousedown                                          |
			| Date Offset          | 2 weeks                                                   |
			| Non Existant Element | thisdoesnotexist                                          |
			| NoUISlider           | #nouislider > div                                         |
			| CaseChange           | abcdefg                                                   |
			| DropDownListIndex    | 2                                                         |
			| ArithmeticTest       | 1                                                         |
			| Trunk SVG            | //*[@id="CNR"]/*[name()="path"][1]                        |
		And I dump the alias map to the console
		And I dump the value of the alias "Non Existant Element" to the console
		And I verify that the alias "this alias does not exist" is empty
	  # Make sure this sequence of configurations works as expected
		And I set autoaliasing to "true"
		And I disable autoaliasing
		And I enable autoaliasing

	@test
	Scenario: Test finding text in page
		And I wait "30" seconds for the page to contain the text "Some text with styles"
		And I scroll to the bottom of the page
	 # IE reports hidden and invisible text
		And I verify that the page contains the regex "Some text with (blah)?hidden elements"
	 # The whitspace before the invisible element could be a space, or a space and line break
		And I verify that the page contains the regex "Some text with\s+(blah)?invisible elements"
		And I verify that the page contains the text "Some text with supertext"
		And I verify that the page contains the text "Some text with subtext"
		And I verify that the page contains the text "Some text with UTF characters like Є and ϼ"
	 # The whitespace between table cells can be tabs, spaces or newlines depending on the browser
		And I verify that the page contains the regex "Some(\s)+text(\s)+in(\s)+a(\s)+table"

	@test @iefail
	Scenario: Click SVG Image in Object
		And I scroll to the bottom of the page
		# Note that the selector to the element in the SVG must be an XPath.
		# The selector to the <object> element can be any of the standard selectors (xpath, css path, id etc).
		And I click the "//*[@id='CNR']/*[name()='path'][1]" element in the object element "/html/body/object"
		Then I verify that the page contains the text "SVG Clicked!"

	@test
	Scenario: Test cookie steps
		And I dump the value of the cookie called "whatever"
		And I delete cookies called "whatever"
		And I delete cookies called "whatever" with the path "/somecontext"
		And I delete all cookies

	@test
	Scenario: Test window resizing
		And I maximise the window
		And I set the window size to "1024x768"

	@test
	Scenario: Test scrolling
		And I scroll to the bottom of the page
		And I scroll to the top of the page

	@test
	Scenario: Test Autoaliasing
	This is actually the default, but test the step anyway
		Given I enable autoaliasing
		And I enable HAR logging
		And I set the default keystroke delay to "0" milliseconds
		And I "mousedown" on the hidden element found by "Event Button"
		And I "mousedown" on the hidden "Event Button" button
		Then I verify that the page contains the text "MouseDown Text"

	@test
	Scenario: Arithmetic
		And I modify the alias "ArithmeticTest" by setting it to "2" if the value it holds is smaller
		Then I verify that the alias "ArithmeticTest" is equal to "2"
		And I modify the alias "ArithmeticTest" by setting it to "1" if the value it holds is larger
		Then I verify that the alias "ArithmeticTest" is equal to "1"
		And I modify the alias "ArithmeticTest" by multiplying "10" with it
		Then I verify that the alias "ArithmeticTest" is equal to "10"
		And I modify the alias "ArithmeticTest" by dividing "2" into it
		Then I verify that the alias "ArithmeticTest" is equal to "5"
		And I modify the alias "ArithmeticTest" by adding "2000" to it
		Then I verify that the alias "ArithmeticTest" is equal to "2005"
		And I modify the alias "ArithmeticTest" by subtracting "1005" from it
		Then I verify that the alias "ArithmeticTest" is equal to "1000"
		And I modify the alias "ArithmeticTest" by multiplying "ArithmeticTest" with it
		Then I verify that the alias "ArithmeticTest" is equal to "1000000"
		And I modify the alias "ArithmeticTest" by dividing "ArithmeticTest" into it
		Then I verify that the alias "ArithmeticTest" is equal to "1"
		And I modify the alias "ArithmeticTest" by adding "ArithmeticTest" to it
		Then I verify that the alias "ArithmeticTest" is equal to "2"
		And I modify the alias "ArithmeticTest" by subtracting "ArithmeticTest" from it
		Then I verify that the alias "ArithmeticTest" is equal to "0"

	@test
	Scenario: Test waiting for non-existent text
		And I wait "1" seconds for the page to contain the text "This text will never exist", ignoring timeouts
		And I wait "1" seconds for the page to contain the regex "This regex will never exist", ignoring timeouts

	@fail-with-one-window
	Scenario: Fail when closing the only window
		And I close the current window

	@test
	Scenario: Test Javascript alias parameters
	The current alias map is made available to JavaScript as a list of arguments, with the
	alias name being followed by the alias value. Here we scan the list of arguments looking
	for a known alias key, and return the value. We then verify that the returned value
	matches the original alias.
		And I run the following JavaScript and save the result to alias "JavaScript Return Value"
	  	"""
	  	if (arguments[0].length % 2 !== 0) {
	  		return "arguments[0] should always be an even number";
	  	}
	  	for (var i = 0; i < arguments[0].length; i += 2) {
			if (arguments[0][i] === 'CaseChange') {
				return arguments[0][i + 1];
			}
	  	}
	  	return "Failed to find alias mapping";
	  	"""
		Then I verify that the alias "JavaScript Return Value" is equal to alias "CaseChange"
		And I run the following JavaScript
        """
	  	console.log('testing javascript');
	  	"""

	@test
	Scenario: Issue 88 Test: https://github.com/AutoGeneral/IridiumApplicationTesting/issues/88
		And I select option number alias "DropDownListIndex" from the drop down list found by "selectList"
		And I select option number alias "DropDownListIndex" from the "selectList" drop down list
		And I save the content of the first selected option from the drop down list found by "selectList" to the alias "Drop Down List Text"
		Then I verify that the alias "Drop Down List Text" is equal to "Option 2"

	@test
	Scenario: Issue 87: https://github.com/AutoGeneral/IridiumApplicationTesting/issues/87
		And I focus on the element found by "doesnotexist" if it exists
		And I focus on the "doesnotexist" text box if it exists

	@test
	Scenario: Test changing alias case
		And I modify the alias "CaseChange" by converting it to upper case
		Then I verify that the alias "CaseChange" is equal to "ABCDEFG"
		And I modify the alias "CaseChange" by converting it to lower case
		Then I verify that the alias "CaseChange" is equal to "abcdefg"

	@test
	Scenario: test advanced UI interaction
	  # This is what it takes to "click" on a jQuery UI slider. We need to send
	  # Both the mousedown and mouseup events.
		And I "mousedown" "50%" horizontally and "50%" vertically within the area of the element found by "slider"
		And I "mousedown" "50%" horizontally and "50%" vertically within the area of the "slider" slider
		And I "mouseup" "50%" horizontally and "50%" vertically within the area of the element found by "slider"
		And I "mouseup" "50%" horizontally and "50%" vertically within the area of the "slider" slider
		And I run the following JavaScript and save the result to alias "Slider Value"
	  	"""
	  	return $( "#slider" ).slider( "value" );
	  	"""
		Then I verify that the alias "Slider Value" is larger than "0"

	  # This is how we would interact with a jQuery UI slider using JavaScript
		And I run the following JavaScript and save the result to alias "Slider Value"
      """
      $("#slider").slider('value', 10);
      return $( "#slider" ).slider( "value" );
      """
		Then I verify that the alias "Slider Value" is larger than "0"

	  # This is how we interact with a noUiSlider (https://refreshless.com/nouislider/)
	  # Note that the element we are interacting with here (found by the NoUISlider alias)
	  # is an element that was created by the noUiSlider library. It is not imediately
	  # obvious which elements should be recieving the events in this case.
		And I "mousedown" "5%" horizontally and "50%" vertically within the area of the element found by "NoUISlider"
		And I "mousedown" "5%" horizontally and "50%" vertically within the area of the "NoUISlider" slider
		And I run the following JavaScript and save the result to alias "NoUISlider Value"
      """
      return document.getElementById('nouislider').noUiSlider.get()[0];
      """
		Then I verify that the alias "NoUISlider Value" is larger than "0"

	@test
	Scenario: Click elements with random IDs
		And I click the element with the attribute of "data-number" with a random number between "1" and "5"
		Then I verify that the page contains the text "Button with random attribute clicked"
		And I click the element with the attribute of "data-attrname" equal to "button"
		Then I verify that the page contains the text "Button with named attr clicked"

	@test
	Scenario: Modify element attributes
		And I modify the element found by "eventButton" by setting the attribute "data-test" to "New Value"
		And I modify the "eventButton" button by setting the attribute "data-test" to "New Value"
		And I modify the element found by "this does not exist" by setting the attribute "data-test" to "New Value" if it exists
		And I modify the "this does not exist" radio button by setting the attribute "data-test" to "New Value" if it exists
		And I save the attribute content of "data-test" from the element found by "eventButton" to the alias "Data Test Attr"
		And I verify that the alias "Data Test Attr" is equal to "New Value"
		And I save the attribute content of "data-test" from the element with the ID of "eventButton" to the alias "Data Test Attr"
		And I verify that the alias "Data Test Attr" is equal to "New Value"

  # https://github.com/AutoGeneral/IridiumApplicationTesting/issues/32
	@test
	Scenario: Immediate wait
		And I wait "0" seconds for the element found by "eventButton" to be displayed
		And I wait "0" seconds for the "eventButton" button to be displayed

  # This scenario is expected to run
	@tag1 @tag2
	Scenario: Tag test
		And I "mouseup" on the hidden element found by "eventButton"
		And I "mouseup" on the hidden "eventButton" button

  # This scenario is expected to run
	@tag3
	Scenario: Tag Test 2
		Then I verify that the page contains the text "Button mouseup"

  # This scenario is not expected to run
	@tag4 @tag5
	Scenario: Tag Test 3
		And I "mousedown" on the hidden element found by "eventButton"
		And I "mousedown" on the hidden "eventButton" button

  # This scenario is expected to run, and the app should show Button mouseup
  # from Scenario: Tag Test 2
	@test @tag4
	Scenario: tag Test 4
		Then I verify that the page contains the text "Button mouseup"

	@test
	Scenario: Test missing elements
		And I wait "1" seconds for the element with the ID of "thisdoesntexist" to not be present
		And I wait "1" seconds for the element with the xpath of "/html/body/div[100]/input[1000000]" to not be displayed
		And I wait "1" seconds for the element with the class of "thisdoesntexist" to not be displayed
		And I wait "1" seconds for the element with the css selector of "thisdoesntexist" to not be displayed
		And I wait "1" seconds for the element found by "thisdoesntexist" to not be present
		And I wait "1" seconds for the "thisdoesntexist" button to not be present
		And I wait "1" seconds for the element found by "thisdoesntexist" to not be displayed
		And I wait "1" seconds for the "thisdoesntexist" checkbox to not be displayed
		And I verify that the element found by "will not exist" is not displayed within "1" second
		And I verify that the "will not exist" drop down list is not displayed within "1" second

	@test
	Scenario: Modify aliased values
		Then I verify that the alias "Test Value 8" is equal to alias "Test Value 9"
		Then I verify that the alias "Test Value 10" is larger than or equal to "12.345"
		Then I verify that the alias "Test Value 10" is smaller than or equal to "12345"
		And I modify the alias "Test Value 4" by removing all characters that match the regex "[^0-9.]"
		Then I verify that the alias "Test Value 4" is equal to "1234.50"
		And I modify the alias "Test Value 4" by replacing all characters that match the regex "1" with "2"
		Then I verify that the alias "Test Value 4" is equal to "2234.50"
		And I modify the alias "Test Value 4" by replacing the first characters that match the regex "2" with "1"
		Then I verify that the alias "Test Value 4" is equal to "1234.50"
		And I modify the alias "Test Value 4" by prepending it with "9"
		Then I verify that the alias "Test Value 4" is equal to "91234.50"
		And I modify the alias "Test Value 4" by appending it with "0"
		Then I verify that the alias "Test Value 4" is equal to "91234.500"
		And I modify the alias "Test Value 4" by replacing all characters that match the regex "(\d+)\.(\d+)" with "$2.$1"
		Then I verify that the alias "Test Value 4" is equal to "500.91234"
		And I copy the alias "Test Value 4" to the alias "Test Value 5"
		And I verify that the alias "Test Value 5" is equal to "500.91234"
		And I modify the alias "Test Value 4" by appending it with " "
		And I modify the alias "Test Value 4" by appending it with alias "Test Value 7"
		Then I verify that the alias "Test Value 4" is equal to "500.91234 Another Value"
		And I modify the alias "Test Value 4" by prepending it with " "
		And I modify the alias "Test Value 4" by prepending it with alias "Test Value 6"
		Then I verify that the alias "Test Value 4" is equal to "Test Value 500.91234 Another Value"
		And I save the current date with the format "dd MMM yyyy" to the alias "Todays Date"
		And I verify that the alias "Todays Date" matches the regex "\d{2} \w{3} \d{4}"
		And I save the current date offset by "1 day" with the format "dd MMM yyyy" to the alias "Tomorrows Date"
		Then I verify that the alias "Tomorrows Date" matches the regex "\d{2} \w{3} \d{4}"
		And I save the current date offset by "-1 day" with the format "dd MMM yyyy" to the alias "Yesterdays Date"
		Then I verify that the alias "Yesterdays Date" matches the regex "\d{2} \w{3} \d{4}"
		And I save the current date offset by "2 years" with the format "dd MMM yyyy" to the alias "Two Years From Now"
		Then I verify that the alias "Two Years From Now" matches the regex "\d{2} \w{3} \d{4}"
		Then I verify that the alias "Tomorrows Date" is not equal to "Yesterdays Date"
		And I modify the alias "Tomorrows Date" by prepending it with "     "
		And I modify the alias "Tomorrows Date" by trimming it
		Then I verify that the alias "Tomorrows Date" matches the regex "^\d{2} \w{3} \d{4}$"
		Then I verify that the alias "Tomorrows Date" is "11" characters long
		And I save the current date offset by "Date Offset" with the format "dd MMM yyyy" to the alias "Two Weeks From Now"
		Then I verify that the alias "Two Weeks From Now" matches the regex "^\d{2} \w{3} \d{4}$"

	@test
	Scenario: Test class verification
		Then I verify the element with the ID of "verifyDivClass" should have a class of "divClass"
		Then I verify the element with the ID of "thisdoesntexist" should have a class of "divClass" if it exists

	@test
	Scenario: test element verification steps
		Then I verify the element found by "output" is present within "2" seconds
		Then I verify the "output" div is present within "2" seconds
		Then I verify the element found by alias "Non Existant Element" is not present within "1" second

		Then I verify the element with the ID of "output" is present
		Then I verify the element with the ID alias of "Non Existant Element" is not present within "1" second
		Then I verify the element with the ID of "output" is displayed
		Then I verify the element with the ID alias of "Non Existant Element" is not displayed within "1" second

		Then I verify the element with the css selector of "#output" is present
		Then I verify the element with the css selector alias of "Non Existant Element" is not present within "1" second

		Then I verify the element with the xpath of "//*[@id='output']" is present
		Then I verify the element with the xpath alias of "Non Existant Element" is not present within "1" second

		Then I verify the element with the name of "buttonName" is present
		Then I verify the element with the name alias of "Non Existant Element" is not present within "1" second

		Then I verify the element with the class of "buttonClass" is present
		Then I verify the element with the class alias of "Non Existant Element" is not present within "1" second

		Then I verify that a link with the text content of "this does not exist" is not present
		Then I verify the element with the attribute of "id" equal to "output" is displayed
		Then I verify the element with the attribute of "id" equal to "this does not exist" is not displayed
		Then I verify the element with the attribute of "id" equal to "this does not exist" is not displayed within "1" second

		Then I verify the element with the attribute of "id" equal to "output" is present
		Then I verify the element with the attribute of "id" equal to "this does not exist" is not present
		Then I verify the element with the attribute of "id" equal to "this does not exist" is not present within "1" second

	@test
	Scenario: Manual Mouse Events
		And I "mousedown" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mousedown"
		Then I verify the element found by "output" is displayed
		Then I verify that the page does not contain the text "This text does not exist"
		Then I verify that the page does not contain the regex "(This)\s\w+does not exist"
		And I "mouseup" on the hidden element found by "eventButton"
		Then I verify that the page contains the regex "(Button)\s(mouseup)"
		And I "mouseover" on the hidden element found by "eventButton"
		Then I wait "30" seconds for the page to contain the text "Button mouseover"
		And I "mouseout" on the hidden element found by "eventButton"
		Then I wait "30" seconds for the page to contain the text "Button mouseout"
		And I "mousemove" on the hidden element found by "eventButton"
		Then I wait "30" seconds for the page to contain the regex "Button mousemove"
		And I "mouseenter" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mouseenter"
		And I "mouseleave" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button mouseleave"

		And I "mousedown" on the hidden "eventButton" button
		Then I verify that the page contains the text "Button mousedown"
		Then I verify the "output" div is displayed
		Then I verify that the page does not contain the text "This text does not exist"
		Then I verify that the page does not contain the regex "(This)\s\w+does not exist"
		And I "mouseup" on the hidden "eventButton" button
		Then I verify that the page contains the regex "(Button)\s(mouseup)"
		And I "mouseover" on the hidden "eventButton" button
		Then I wait "30" seconds for the page to contain the text "Button mouseover"
		And I "mouseout" on the hidden "eventButton" button
		Then I wait "30" seconds for the page to contain the text "Button mouseout"
		And I "mousemove" on the hidden "eventButton" button
		Then I wait "30" seconds for the page to contain the regex "Button mousemove"
		And I "mouseenter" on the hidden "eventButton" button
		Then I verify that the page contains the text "Button mouseenter"
		And I "mouseleave" on the hidden "eventButton" button
		Then I verify that the page contains the text "Button mouseleave"

	@test
	Scenario: Manual Key Events
		And I "keydown" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button keydown"
		And I "keyup" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button keyup"
		And I "keypress" on the hidden element found by "eventButton"
		Then I verify that the page contains the text "Button keypress"
		And I dispatch a "keydown" event on the hidden element with the ID of "eventButton"
		Then I verify that the page contains the text "Button keydown"
		And I dispatch a "keyup" event on the hidden element with the ID of "eventButton"
		Then I verify that the page contains the text "Button keyup"
		And I dispatch a "keypress" event on the hidden element with the ID of "eventButton"
		Then I verify that the page contains the text "Button keypress"

		And I "keydown" on the hidden "eventButton" button
		Then I verify that the page contains the text "Button keydown"
		And I "keyup" on the hidden "eventButton" button
		Then I verify that the page contains the text "Button keyup"
		And I "keypress" on the hidden "eventButton" button
		Then I verify that the page contains the text "Button keypress"

	@test
	Scenario: Work with DropDown Lists
		And I select "Option 2" from the drop down list found by "selectList"
		And I save the content of the first selected option from the drop down list found by "selectList" to the alias "Drop Down List Text"
		Then I verify that the alias "Drop Down List Text" is equal to "Option 2"
		And I select "Option 2" from the drop down list with the ID of "selectList"
		And I save the content of the first selected option from the drop down list found by "selectList" to the alias "Drop Down List Text"
		Then I verify that the alias "Drop Down List Text" is equal to "Option 2"
		And I save the content of the first selected option from the drop down list with the ID of "selectList" to the alias "Drop Down List Text"
		Then I verify that the alias "Drop Down List Text" is equal to "Option 2"
		And I save the value of the first selected option from the drop down list found by "selectList" to the alias "Drop Down List Value"
		Then I verify that the alias "Drop Down List Value" is equal to "Option2"
		And I save the value of the first selected option from the drop down list with the ID of "selectList" to the alias "Drop Down List Value"
		Then I verify that the alias "Drop Down List Value" is equal to "Option2"
	# This step adds "if it exists" because the Firefox Marionette driver has a bug
	# that will cause this step to fail.
		And I select option number "3" from the drop down list found by "selectList" if it exists
		And I select option number "100" from the drop down list found by "this does not exist" if it exists
		And I select option number "3" from the drop down list with the ID of "selectList" if it exists

		And I select "Option 2" from the "selectList" drop down list
		And I save the content of the first selected option from the "selectList" drop down list to the alias "Drop Down List Text"
		Then I verify that the alias "Drop Down List Text" is equal to "Option 2"
		And I select "Option 2" from the drop down list with the ID of "selectList"
		And I save the content of the first selected option from the "selectList" drop down list to the alias "Drop Down List Text"
		Then I verify that the alias "Drop Down List Text" is equal to "Option 2"
		And I save the value of the first selected option from the "selectList" drop down list to the alias "Drop Down List Value"
		Then I verify that the alias "Drop Down List Value" is equal to "Option2"

	# This step adds "if it exists" because the Firefox Marionette driver has a bug
	# that will cause this step to fail.
		And I select option number "3" from the "selectList" drop down list if it exists
		And I select option number "100" from the "this does not exist" drop down list if it exists

	@test
	Scenario: Focus on Elements
		And I focus on the element found by "this does not exist" if it exists
		And I focus on the "this does not exist" text area if it exists
		And I focus on the element with the ID of "his does not exist" if it exists
		And I focus on the element found by "textArea"
		And I focus on the "textArea" text area
		And I press CTRL-A on the active element
		And I press CMD-A on the active element
		And I press the Delete on the active element "10" times
		And I press the down arrow key on the active element
		And I press the up arrow key on the active element
		And I press the left arrow key on the active element
		And I press the right arrow key on the active element
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

		And I focus on the alias "Text Area 1" text area
		Then I verify that the page contains the text "Focused on textarea"
		And I populate the "textArea" text area with "Some Text"
		And I select all the text in the active element ignoring errors
		And I populate the "textArea" text area with "New Line"
		And I press the tab key on the active element ignoring errors

	@test
	Scenario: Test Clicking Elements
		And I verify that the element found by "buttonId" is clickable
		And I verify that the element with the ID of "buttonId" is clickable within "10" seconds
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

		And I verify that the "buttonId" button is clickable
		And I verify that the element with the ID of "buttonId" is clickable within "10" seconds
		And I click the "buttonId" button
		Then I verify that the page contains the text "Button By ID Clicked"
		And I click the "buttonClass" button
		Then I verify that the page contains the text "Button By Class Clicked"
		And I click the "buttonName" button
		Then I verify that the page contains the text "Button By Name Clicked"
		And I click the "buttonValue" button
		Then I verify that the page contains the text "Button By Value Clicked"
		And I click the "Button By Text" button
		Then I verify that the page contains the text "Button By Text Clicked"

	@test
	Scenario: Test Clicking Elements multiple times
		And I save the current date with the format "MM" to the alias "This Month"
		And I save the current date offset by "2 month" with the format "MM" to the alias "Next Month"
		And I modify the alias "Next Month" by subtracting alias "This Month" from it
		And I copy the alias "Next Month" to the alias "Click Repeat"
		And I modify the alias "Click Repeat" by setting it to "2" if the value it holds is smaller

		And I click the element found by "buttonId" "Click Repeat" times
		Then I verify that the page contains the text "Button By ID Clicked"
		And I click the element found by "buttonClass" "Click Repeat" times
		Then I verify that the page contains the text "Button By Class Clicked"
		And I click the element found by "buttonName" "Click Repeat" times
		Then I verify that the page contains the text "Button By Name Clicked"
		And I click the element found by "buttonValue" "Click Repeat" times
		Then I verify that the page contains the text "Button By Value Clicked"
		And I click the element found by "Button By Text" "Click Repeat" times
		Then I verify that the page contains the text "Button By Text Clicked"
		And I click the element with the ID of "buttonId" "Click Repeat" times
		Then I verify that the page contains the text "Button By ID Clicked"
		And I click the element with the class of "buttonClass" "Click Repeat" times
		Then I verify that the page contains the text "Button By Class Clicked"
		And I click the element with the name of "buttonName" "Click Repeat" times
		Then I verify that the page contains the text "Button By Name Clicked"

		And I click the "buttonId" button "Click Repeat" times
		Then I verify that the page contains the text "Button By ID Clicked"
		And I click the "buttonClass" button "Click Repeat" times
		Then I verify that the page contains the text "Button By Class Clicked"
		And I click the "buttonName" button "Click Repeat" times
		Then I verify that the page contains the text "Button By Name Clicked"
		And I click the "buttonValue" button "Click Repeat" times
		Then I verify that the page contains the text "Button By Value Clicked"
		And I click the "Button By Text" button "Click Repeat" times
		Then I verify that the page contains the text "Button By Text Clicked"

	@test
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

		And I click the alias "Button ID" button
		Then I verify that the page contains the text alias "Button ID Output"
		And I click the alias "Button Class" button
		Then I verify that the page contains the text alias "Button Class Output"
		And I click the alias "Button Name" button
		Then I verify that the page contains the text alias "Button Name Output"
		And I click the alias "Button Value" button
		Then I verify that the page contains the text alias "Button Value Output"
		And I click the alias "Button Text" button
		Then I verify that the page contains the text alias "Button Text Output"

	@test
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

		And I click the hidden "buttonId" button
		Then I verify that the page contains the text "Button By ID Clicked"
		And I click the hidden "buttonClass" button
		Then I verify that the page contains the text "Button By Class Clicked"
		And I click the hidden "buttonName" button
		Then I verify that the page contains the text "Button By Name Clicked"
		And I click the hidden "buttonValue" button
		Then I verify that the page contains the text "Button By Value Clicked"
		And I click the hidden "Button By Text" button

	@test
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

		And I click the hidden alias "Button ID" button
		Then I verify that the page contains the text alias "Button ID Output"
		And I click the hidden alias "Button Class" button
		Then I verify that the page contains the text alias "Button Class Output"
		And I click the hidden alias "Button Name" button
		Then I verify that the page contains the text alias "Button Name Output"
		And I click the hidden alias "Button Value" button
		Then I verify that the page contains the text alias "Button Value Output"
		And I click the hidden alias "Button Text" button

	@test
	Scenario: Test verification steps
		And I verify that the element found by "textClass" should have a class of "textClass"
		And I verify that the "textClass" text box should have a class of "textClass"

	@test
	Scenario: Test Populating Inputs 1
		And I populate the element found by "textId" with "Text Box Found By ID"
		And I clear the element with the ID of "textId"
		And I populate the "textId" text box with "Text Box Found By ID"
		And I clear the element with the ID of "textId"
		And I populate the element found by "textClass" with "Text Box Found By Class" with a keystroke delay of "100" milliseconds
		And I clear the element with the class of "textClass"
		And I populate the "textClass" text box with "Text Box Found By Class" with a keystroke delay of "100" milliseconds
		And I clear the element with the class of "textClass"
		And I populate the element found by "textName" with "Text Box Found By Name" with a keystroke delay of "50" milliseconds
		And I clear the element with the name of "textName"
		And I populate the element found by "textName" with "Text Box Found By Name" with a keystroke delay of "50" milliseconds
		And I clear the element with the name of "textName"
		And I populate the "textValue" text box with "Text Box Found By Value" with a keystroke delay of "25" milliseconds
		And I clear the "textName" text box
		And I clear the hidden "textId" text box

	@test
	Scenario: Test Populating Inputs 2
		And I populate the element found by "textId" with a random number between "1" and "10"
		And I clear the hidden element found by "textId"
		And I populate the hidden element found by "textId" with "Hidden Text Box Found By ID"
		And I clear the "textId" text box
		And I populate the "textId" text box with a random number between "1" and "10"
		And I clear the hidden element found by "textId"
		And I clear the element found by "textClass"
		And I clear the element found by "textName"
		And I clear the element with the xpath alias of "Text Box xpath"
		And I populate the element with the ID of "textId" with "Text Box Found By ID"
		And I populate the element with the class of "textClass" with "Text Box Found By Class" with a keystroke delay of "100" milliseconds
		And I populate the element with the name of "textName" with "Text Box Found By Name" with a keystroke delay of "50" milliseconds
		And I populate the element with the css selector alias of "Text Box css" with "Text Box Found By CSS Selector" with a keystroke delay of "25" milliseconds
		And I populate the element with the xpath alias of "Text Box xpath" with " And Then With An XPath" with a keystroke delay of "25" milliseconds
		And I clear the element with the ID of "textId"
		And I populate the element with the ID of "textId" with a random number between "1" and "10"
		And I clear the element with the ID of "textId"
		And I populate the element with the ID of "textId" with a random number between "1" and "10"
		And I clear the element with the ID of "textId"
		And I populate the hidden element with the ID of "textId" with "Text Box Found By ID"
		And I clear the element with the ID of "textId"
		And I populate the element with the attribute of "name" equal to "textName" with "Text Box Found By Name"
		And I clear the element with the ID of "textId"

	@test
	Scenario: Test Populating Inputs 3
		And I populate the element with the ID of "formText" with "Text Box Found By ID" and submit
		Then I verify that the page contains the text "Form submitted"
		And I populate the element found by "formText" with "Text Box Found By ID" and submit
		And I populate the "formText" text box with "Text Box Found By ID" and submit
		Then I verify that the page contains the text "Form submitted"
		And I clear the element with the ID of "textId"
		And I clear the element with the class of "textClass"
		And I clear the element with the name of "textName"

	@test
	Scenario: Populate Inputs That Are Missing
		And I populate the element found by "thisDoesntExist" with "Whatever" if it exists
		And I populate the "thisDoesntExist" text box with "Whatever" if it exists
		And I populate the element found by alias "Non-Existant Field" with "Whatever" if it exists
		And I populate the alias "Non-Existant Field" text box with "Whatever" if it exists
		And I populate the element with the ID of "thisDoesntExist" with "Whatver" if it exists
		And I populate the element with the ID alias of "Non-Existant Field" with "Whatver" if it exists

	@test
	Scenario: Clicking missing elements
		And I click the element found by "thisDoesntExist" if it exists
		And I click the "thisDoesntExist" button if it exists
		And I click the element found by alias "Non-Existant Field" if it exists
		And I click the alias "Non-Existant Field" button if it exists
		And I click the element with the ID of "thisDoesntExist" if it exists
		And I click the element with the class of "thisDoesntExist" if it exists
		And I click the element with the name of "thisDoesntExist" if it exists
		And I click the element with the ID alias of "Non-Existant Field" if it exists
		And I click the element with the class alias of "Non-Existant Field" if it exists
		And I click the element with the name alias of "Non-Existant Field" if it exists
		And I click the hidden element found by "thisDoesntExist" if it exists
		And I click the hidden "thisDoesntExist" button if it exists
		And I click the hidden element found by alias "Non-Existant Field" if it exists
		And I click the hidden alias "Non-Existant Field" button if it exists
		And I click the hidden element with the ID of "thisDoesntExist" if it exists
		And I click the hidden element with the class of "thisDoesntExist" if it exists
		And I click the hidden element with the name of "thisDoesntExist" if it exists
		And I click the hidden element with the ID alias of "Non-Existant Field" if it exists
		And I click the hidden element with the class alias of "Non-Existant Field" if it exists
		And I click the hidden element with the name alias of "Non-Existant Field" if it exists

	Scenario: Verify Element Has Class
		Then I verify that the element found by "verifyDivClass" should have a class of "divClass"
		Then I verify that the "verifyDivClass" div should have a class of "divClass"
		And I verify that the element with the ID of "verifyDivClass" should have a class of "divClass"
		And I verify that the element found by "#verifyDivClass" should have a class of "divClass"
		And I verify that the "#verifyDivClass" div should have a class of "divClass"
		And I verify that the element with the css selector of "#verifyDivClass" should have a class of "divClass"
		And I verify that the element found by "divClass" should have a class of "divClass"
		And I verify that the "divClass" div should have a class of "divClass"
		And I verify that the element with the class of "divClass" should have a class of "divClass"
		And I verify that the element found by "//*[@id='verifyDivClass']" should have a class of "divClass"
		And I verify that the "//*[@id='verifyDivClass']" div should have a class of "divClass"
		And I verify that the element with the xpath of "//*[@id='verifyDivClass']" should have a class of "divClass"
		And I verify that the element found by "A div with a class" should have a class of "divClass"
		And I verify that the "A div with a class" div should have a class of "divClass"

	@test
	Scenario: Verify Page
		And I verify that the browser title should be "Iridium Test Page"

	@test
	Scenario: Save Values as Aliases and Verify Them
		And I save the text content of the element found by "verifyString" to the alias "Example String"
		And I save the text content of the "verifyString" text box to the alias "Example String"
		And I save the text content of the element found by "verifyNumber" to the alias "Example Number"
		And I save the text content of the "verifyNumber" text box to the alias "Example Number"
		And I save the text content of the element with the ID of "verifyNumber" to the alias "Example Number"
		And I save the attribute content of "data-verify" from the element found by "verifyNumber" to the alias "Example Number Attr"
		And I save the attribute content of "data-verify" from the "verifyNumber" text box to the alias "Example Number Attr"
		And I save the attribute content of "data-verify" from the element found by "verifyString" to the alias "Example String Attr"
		And I save the attribute content of "data-verify" from the "verifyString" text box to the alias "Example String Attr"
		And I save the value of the element found by "optionValue" to the alias "Option Value"
		Then I verify that the alias "Example Number" is a number
		And I save the value of the element with the ID of "optionValue" to the alias "Option Value"
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
		And I verify that the alias "Test Value 1" is smaller than the alias "Test Value 2"
		And I verify that the alias "Test Value 1" is smaller than or equal to the alias "Test Value 3"
		And I verify that the alias "Test Value 2" is larger than the alias "Test Value 3"
		And I verify that the alias "Test Value 1" is larger than or equal to the alias "Test Value 3"

	@test
	Scenario: Save Values from Hidden Elements
		And I save the text content of the hidden element found by "verifyString" to the alias "Example String"
		And I save the text content of the hidden "verifyString" text box to the alias "Example String"
		And I save the text content of the hidden element with the ID of "verifyString" to the alias "Example String"
		And I verify that the alias "Example String" matches the regex "[a-zA-Z]+"

	@test
	Scenario: Click Links
		And I wait "30" seconds for a link with the text content of alias "Link Contents" to be present
		And I wait "2" seconds for a link with the text content of "This link does not exist" to not be present
		And I click the link with the text content of "Test Link"
	  # Firefox will often fail trying to click this link twice in a row for some reason with the error:
	  # org.openqa.selenium.StaleElementReferenceException: The element reference of <a> stale: either the
	  # element is no longer attached to the DOM or the page has been refreshed
	  # So we sleep for a bit before clicking again
		And I sleep for "1" second
		And I click the hidden link with the text content alias of "Link Contents"

	@test
	Scenario: Navigate
	# Note that Safari doesn't support this kind of navigation
		And I go back ignoring errors
		And I go forward ignoring errors
		And I go to the hash location "whatever"
		And I go back ignoring errors
		And I refresh the page ignoring errors

	@test
	Scenario: Wait steps
		And I wait "30" seconds for the element found by "verifyDivClass" to be displayed
		And I wait "30" seconds for the "verifyDivClass" div to be displayed
		And I wait "1" seconds for the element found by "thisDoesntExist" to be displayed ignoring timeouts
		And I wait "1" seconds for the "thisDoesntExist" div to be displayed ignoring timeouts
		And I wait "2" seconds for the element with the attribute of "id" equal to "verifyDivClass" to be displayed
		And I wait "2" seconds for the element with the attribute of "id" equal to "verifyDivClass" to be present
		And I wait "30" seconds for the element with the attribute of "id" equal to "this does not exist" to not be displayed
		And I wait "30" seconds for the element with the attribute of "id" equal to "this does not exist" to not be present
		And I wait "30" seconds for the element with the ID of "verifyDivClass" to be displayed
		And I wait "1" seconds for the element with the ID alias of "Non-Existant Field" to be displayed ignoring timeouts
		And I sleep for "1" second
		And I wait "30" seconds for the element found by alias "Button ID" to be present
		And I wait "30" seconds for the alias "Button ID" button to be present
		And I wait "30" seconds for the element with the ID alias of "Button ID" to be present
		And I wait "30" seconds for the element found by alias "Button ID" to be clickable
		And I wait "30" seconds for the alias "Button ID" button to be clickable
		And I wait "30" seconds for the element with the ID of "Button ID" to be clickable
		And I wait "1" seconds for the element found by alias "Non-Existant Field" to be present ignoring timeouts
		And I wait "1" seconds for the alias "Non-Existant Field" div to be present ignoring timeouts
		And I wait "1" seconds for the element found by alias "Non-Existant Field" to be clickable ignoring timeouts
		And I wait "1" seconds for the alias "Non-Existant Field" div to be clickable ignoring timeouts

	@test
	Scenario: Save HAR file
		And I dump the HAR file to "test.har"
		And I save the current date with the format "ddMMyyyyHHmmssSSS" to the alias "Har File Name"
		And I modify the alias "Har File Name" by prepending it with "test"
		And I modify the alias "Har File Name" by appending it with ".har"
		Then I dump the HAR file to "Har File Name"

	@test
	Scenario: Test step skipping
		And I skip all remaining steps
	 # This is skipped, so there is no failure
		And I fail the scenario

	@test
	Scenario: Scroll Test
		And I scroll to the bottom of the page
		And I scroll to the top of the page

	@test
	Scenario: Modify HTTP request headers
		And I set header "X-Forwarded-For" with value "127.0.0.1"
		And I remove header "User-Agent"

	@test
	Scenario: Test alert handling
		And I click the element found by "alertButton"
		And I wait "30" seconds for an alert to be displayed
		And I click "OK" on the alert
		And I click the "alertButton" button
		And I wait "30" seconds for an alert to be displayed
		And I click "OK" on the alert
		And I click the element found by "confirmButton"
		And I wait "30" seconds for an alert to be displayed
		And I click "Cancel" on the alert
		And I click the "confirmButton" button
		And I wait "30" seconds for an alert to be displayed
		And I click "Cancel" on the alert
	 # Timeouts should be ignored if there is no alert
		And I wait "2" seconds for an alert to be displayed, ignoring timeouts
		And I click "OK" on the alert if it exists
		And I click "Cancel" on the alert if it exists

	@test @linktest
	Scenario: Issue 90 Test: https://github.com/AutoGeneral/IridiumApplicationTesting/issues/90
		And I open the link with the text content of "Test Link" in a new window
		And I sleep for "5" seconds
	  # Remote tests must allow popups e.g. browserstack.ie.enablePopups = true
	# https://www.browserstack.com/automate/capabilities
		And I switch to the new window ignoring errors
		And I close the current window ignoring errors
