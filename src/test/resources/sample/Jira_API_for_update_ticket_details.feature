Feature: Using Jira API for update ticket details
  #@JiraAPI
  Scenario: Update summary and labels through Jira ID
    Then the user updates information on jira ticket
      | Jira ID    | Summary                                                        | Label to add                         | Label to remove    |
      | TEST-18221 | Create step to update label and summary of jira id through api |                                      |                    |
      | TEST-3775  | Smoketest_Sky_Q                                                |                                      |                    |
      | TEST-3776  | Smoketest_Sky_Q                                                | test_label                           |                    |
      | TEST-3777  |                                                                | test_label                           |                    |
      | TEST-3778  |                                                                | test_label                           | test               |
      | TEST-3779  |                                                                |                                      | test               |
      | TEST-3780  | Smoketest_Sky_Q                                                |                                      | test               |
      | TEST-3781  | Smoketest_Partner_Portal                                       | test_label                           | test               |
      | TEST-3782  | Smoketest_Partner_Portal                                       | test_label,test_label_2              | test               |
      | TEST-3783  | Smoketest_Partner_Portal                                       | test_label,test_label_2,test_label_3 | test               |
      | TEST-3784  | Smoketest_Partner_Portal                                       | test_label                           | test,test_2        |
      | TEST-3785  | Smoketest_Partner_Portal                                       | test_label                           | test,test_2,test_3 |
      | TEST-3785  | Smoketest_Partner_Portal                                       | test_label,test_label_2,test_label_3 | test,test_2,test_3 |
