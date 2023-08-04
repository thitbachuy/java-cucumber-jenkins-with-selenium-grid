Feature: Browser handling

  @TEST-browser
  Scenario: Browser Testing
    Given the user opens "firefox" browser with session alias "firefox 1"
    Then the user can open "@TD:FE_DELoginPage"
    Given the user opens "firefox" browser with session alias "firefox 2"
    Then the user can open "@TD:FEProduct_SkyQ"
    When the user switches to browser "initial"
    Then the user can open "@TD:FE_AustrianLoginPage"
