package steps.desktop;

import config.DriverUtil;
import config.FilesUtils;
import config.TestDataLoader;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import pages.dekstop.BasicPage;
import steps.Hook;

public class BasicSteps {

  private BasicPage basicPage;


  public BasicSteps() {
    basicPage = new BasicPage(DriverUtil.getDriver());
  }

  @Given("the user can open {string}")
  public void the_user_can_open_string(String url) {
    basicPage.openURL(url);
  }

  @Given("the current url is {string}")
  public void the_current_url_is_string(String url) {
    basicPage.verifyCurrentUrl(url);
  }

  @Given("the user can open {string} in new window")
  public void the_user_can_open_string_in_new_window(String url) {
    basicPage.openUrlNewWindow(url);
    basicPage.waitForPageLoaded();
  }

  @And("the user switch to tab {string}")
  public void the_user_switch_to_tab_string(String index) {
    Integer idx = Integer.parseInt(index);
    basicPage.switchToTabByIndex(idx);
  }

  @And("the user switch to tab {string} and close tab {string}")
  public void the_user_switch_to_tab_string_and_close_tab_string(String index1, String index2) {
    basicPage.switchToTabByIndexAndCLoseByIndex(Integer.parseInt(index1), Integer.parseInt(index2));
  }

  @When("the user switch to first tab")
  public void the_user_switch_to_tab_string() {
    basicPage.switchToFirstTab();
  }

  @When("the user switch to first tab and close second")
  public void the_user_switch_to_tab_string_and_close_second() {
    basicPage.switchToFirstTabAndClose();
  }

  @When("the user switches to tab {string}")
  public void the_user_switches_to_tab_string(String tab) {
    basicPage.switchToTabX(Integer.parseInt(tab));
  }

  @Then("the user sees page {string}")
  public void the_user_sees_page_string(String page) {
    basicPage.checkLandingPage(page);
  }

  @Then("the user sees page {string} with url {string} and title {string}")
  public void the_user_sees_page_string_with_url_string_and_title_string(String page, String url,
    String title) {
    basicPage.checkLandingPageWithUrlAndTitle(page, url, title);
  }

  @When("the user goes one page back")
  public void the_user_goes_one_page_back() {
    basicPage.goOnePageBack();
  }

  @When("the user refresh current page")
  public void the_user_refresh_current_page() {
    basicPage.refresh();
  }

  @When("the user switch to salesforce tab {string}")
  public void the_user_switch_to_salesforce_tab_string(String tab) {
    basicPage.switchTab(tab);
  }

//  @Then("the user open browser {string}")
//  public void openBrowser(String browserName) {
//    DriverUtil.openBrowser(browserName);
//  }
//
//  @Then("the user open browser {string} with incognito")
//  public void openBrowserWithIncognito(String browserName) {
//    DriverUtil.openBrowserWithIncognito(browserName);
//  }
//
//  @Then("open browser {string} with incognito and proxy")
//  public void openBrowserWithIncognitoAndProxy(String browserName) {
//    DriverUtil.openBrowserWithProxy(browserName);
//  }
//
//  @Given("when browser is configured for proxy and open {string}")
//  public void openBrowserWithProxy(String url) {
//    String browser = Hook.browser;
//    DriverUtil.openBrowserWithProxy(browser);
//    basicPage.openURL(basicPage.loadUrl(url));
//  }

  @Given("the user switches to browser {string}")
  public void the_user_switches_to_browser_string(String browserName) {
    DriverUtil.switchToBrowser(TestDataLoader.getTestData(browserName));
  }

  @Then("the user closes browser {string}")
  public void the_user_close_browser(String browserName) {
    DriverUtil.closeBrowser(browserName);
  }


  @And("current page has element {string} with text {string}")
  public void current_page_has_element_with_text(String element, String text) {
    basicPage.pageHasElementWithText(element, text);
  }


  @Then("current page url contains {string}")
  public void current_page_url_contains(String urlPart) {
    basicPage.currentPageUrlContains(urlPart);
  }

  @Then("the user sees text {string}")
  public void the_user_sees_text(String text) {
    basicPage.currentPageHasText(text);

  }

  @And("the user closes current page")
  public void the_user_closes_current_page() {
    basicPage.closeCurrentPage();
  }

  @Then("the user waits {string} minutes on current page")
  public void the_user_waits_string_minutes_on_current_page(String time) {

    basicPage.waitFor(Integer.parseInt(time)).minutes();
  }


  @Given("the user generates a random email address {string} at server {string}")
  public void the_user_generates_a_random_email_address_string_at_server_string(String mailAddress,
    String mailServer) {

    basicPage.generateRandomEmailAtServer(mailAddress, mailServer);
  }

  @And("the user saves {string} with value {string}")
  public void the_user_saves_string_with_value_string(String key, String value) {

    basicPage.saveKeyWithValue(key, value);
  }

  @Then("the user waits for {string} {string} on current page")
  public void the_user_waits_for_string_string_on_current_page(String timeout, String timeUnit) {

    switch (timeUnit) {
      case "milliseconds":
        basicPage.waitFor(Integer.parseInt(timeout)).milliseconds();
        break;
      case "seconds":
        basicPage.waitFor(Integer.parseInt(timeout)).seconds();
        break;
      case "minutes":
        basicPage.waitFor(Integer.parseInt(timeout)).minutes();
        break;
      case "days":
        basicPage.waitFor(Integer.parseInt(timeout)).days();
        break;
      default:
        throw new CucumberException(
          "Invalid time unit. The time unit should be the following: {'milliseconds', 'seconds', 'minutes', 'days'}");
    }
  }

  @And("the user opens browser with user agent {string}")
  public void the_user_opens_browser_with_user_agent_string(String userAgent) {

    basicPage.openBrowserWithUserAgent(userAgent);
  }

  @And("the user switches focus to tab {string}")
  public void the_user_switches_focus_to_new_tab(String pageTitle) {

    basicPage.switchDriverToTab(pageTitle);
  }

  @And("the user ensures that file {string} is {string}")
  public void the_user_ensures_that_file_string_is_string(String fileName, String existence) {

    basicPage.ensureFileExistenceAsExpectation(fileName, existence);
  }

  @Given("the user opens new browser with alias {string}")
  public void the_user_opens_new_browser_with_alias_string(String browserAlias) {

    basicPage.openNewBrowserWithAlias(browserAlias);
  }

  @And("the user zooms browser to {string}")
  public void the_user_zooms_browser_to_string(String percent) {

    basicPage.zoomInOut(percent);
  }


  @And("the user verifies that the contents of the file {string} have the following information")
  public void the_user_read_that_file_string_is_string(String filePath, DataTable dataTable) {

    basicPage.verifyContentOfTextFile(filePath, dataTable);
  }

  @And("the user closes tab {string}")
  public void the_user_closes_tab_string(String tabIndex) {

    basicPage.closeTab(Integer.parseInt(tabIndex));
  }

  /**
   * @param context   {salesforce account detail page,salesforce contact detail page,tbd..}
   * @param tableName {name of the table inside context}
   */
  @When("the user {string} following information in the table {string} on {string}")
  public void the_user_string_following_information_in_the_table_string_on_string(String action,
    String tableName, String context, DataTable dataTable) {

    List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
    basicPage.verifyInformationInTable(action, tableName, context, data);
  }

  @And("the user substring {string} from position {int} to {int} and saves as key {string}")
  public void the_user_substring_string_from_position_int_to_int_and_saves_as_key_string(
    String string, int startIndex, int endIndex, String keyToSave) {

    basicPage.subStringAndSaveInDataRunTime(string, startIndex, endIndex, keyToSave);
  }

//  @When("the user generates test set and test execution json files")
//  public void the_user_generates_test_set_and_test_execution_json_files() {
//    (new FilesUtils()).createTestSetAndTestExecutionJsonFile();
//  }

  @When("the user gets output of devtool console command {string} on current page")
  public void the_user_gets_output_of_devtool_console_command_string_on_current_page(
    String command) {

    basicPage.executeJsAndSaveValue(command);
  }

  @And("the user ensures that {string} {string} {string}")
  public void the_user_ensures_that_string_string_string(String value1, String comparison,
    String value2) {

    basicPage.compareTwoStrings(value1, value2, comparison);
  }

  @Given("the user removes the name field from tags node in the generated cucumber json file")
  public void the_user_removes_the_name_field_from_tags_node_in_the_generated_cucumber_json_file() {
    new FilesUtils().updateCucumberJsonFile();
  }

  @Given("the user opens {string} browser with session alias {string}")
  public void the_user_opens_string_browser_with_session_alias_string(String browserName,
    String alias) {

    DriverUtil.initializeBrowserWithSessionAlias(browserName, alias);
  }

  @Given("set the accept cookies popup value to {string}")
  public void set_the_accept_cookies_popup_value_to_string(String isCookieAccepted) {
    Hook.threadLocalCookieAccepted.set(Boolean.valueOf(isCookieAccepted));
  }
}
