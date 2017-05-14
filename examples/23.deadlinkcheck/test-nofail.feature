Feature: Open an application

  # Open up the web page
  Scenario: Launch App
    And I set the default wait time between steps to "2"
    And I open the application ignoring timeouts
    And I maximise the window
    # This step find all the <a> elements and opens all their href urls
    And I open all links in new tabs and then close the tabs

  Scenario: Verify Response Codes
    # This step reports on any http requests that resulted in HTTP errors.
    # Typically, when used in conjunction with the
    # "I open all links in new tabs and then close the tabs" step, these
    # errors means your site has dead links.
    And I report any HTTP errors
