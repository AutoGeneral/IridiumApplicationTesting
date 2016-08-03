Feature: Open an application

 # This is where we give readable names to the xpaths, ids, classes, name attributes or
 # css selectors that this test will be interacting with.
  Scenario: Generate Page Object
    Given the alias mappings
      | SearchMenu  | dropdownMenu2 |
      | SearchField | search        |

  Scenario Outline: Test the search box
	And I open the application
	And I maximise the window
	And I wait "30" seconds for the element found by "SearchMenu" to be displayed
	And I click the element found by alias "SearchMenu"
    And I clear the hidden element with the ID alias of "SearchField"
    And I populate the element found by alias "SearchField" with "<search>"
    Examples:
      | search |
      | Java   |
      | Devops |
      | Linux  |
      | Agile  |
