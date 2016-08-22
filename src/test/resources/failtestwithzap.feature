Feature: A test that starts ZAP and will fail
	Scenario: Launch App
		Given a scanner with all policies enabled
		And I fail the scenario
