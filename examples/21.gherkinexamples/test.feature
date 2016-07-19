Feature: Open an application

 # This is where we give readable names to the xpaths, ids, classes, name attributes or
 # css selectors that this test will be interacting with.
  Scenario: Generate Page Object
    Given the alias mappings
      | SearchMenu  | dropdownMenu2 |
      | SearchField | search        |

  # Open up the web page
  Scenario: Launch App
    And I set the default wait time between steps to "2"
    And I open the application
    And I maximise the window
    And I click the element found by alias "SearchMenu"

  Scenario Outline: Test the search box
    And I clear the hidden element found by alias "SearchField"
    And I populate the element found by alias "SearchField" with "<search>"

    Examples:
      | search |
      | Java   |
      | Devops |
      | Agile  |
      | Linux  |
