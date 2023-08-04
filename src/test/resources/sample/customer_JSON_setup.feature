Feature: Extract Siebel Customer Data from DB and create JSON file

  #@siebel_cust
  Scenario: Query Siebel Customer Data and create JSON file

    Then the user prepares json file for first billing on siebel database page
    Then the user prepares json file for installation and subscription on siebel database page
    Then the user generates batch json file for customer "craucraunouddaubre-3691@yopmail.com" with receiver connection "1" and multiscreenflag "true" on siebel database page
    Then the user retrieves siebel cust number for customer 'aelbert.sordi@yandex.com' on siebel database page

    Then the user generates realtime json file for customer 'Hans-Joerg.Brockmeyer@nttdata.com' on siebel database page


    And the user uploads JSON file to bucket via gcs api call
    And the user waits for "25" minutes max to make sure the JSON file was processed via gcs api call