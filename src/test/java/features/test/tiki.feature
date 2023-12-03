Feature: [Web] Tiki

  @Tiki
  Scenario: [TK] Search tiki 1
    Given the user can open the link "https://www.tiki.vn/"
    When the user enters "tiki 1" into "search tiki" input

  @Tiki
  Scenario: [TK] Search tiki 2
    Given the user can open the link "https://www.tiki.vn/"
    When the user enters "tiki 2" into "search tiki" input
