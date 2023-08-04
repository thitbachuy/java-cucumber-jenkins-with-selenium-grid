package steps.ios;

import config.DriverUtil;
import config.TestDataLoader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.ios.IosSafariPage;

public class IosSafariSteps {

  private static IosSafariPage iosSafariPage;
  public static final String CONTEXT = "ios safari app";
  private static final Logger LOG = LogManager.getLogger(IosSafariSteps.class);

  private void initializePageObject() {
    iosSafariPage = new IosSafariPage(DriverUtil.getIosDriver());
  }

  @Given("the user has removed existing data of " + CONTEXT)
  public void the_user_has_removed_existing_data_of_ios_safari_app() {
    initializePageObject();
    iosSafariPage.removesafariData();
  }

  /**
   * Examples:  page = sky contract page, url = contract help url, title = contract help title
   */
  @Then("the user sees page {string} with url {string} and title {string} on " + CONTEXT)
  public void the_user_sees_page_string_with_url_string_and_title_string_on_ios_safari_app(
      String page, String url, String title) {
    initializePageObject();
    iosSafariPage.verifyLandingPageUrlTitle(page, url, title);
  }


  @When("the user enters {string} in textfield {string} on {string} page on " + CONTEXT)
  public void the_user_enters_string_in_textfield_string_on_string_page_on_ios_safari_app(
      String value, String textfield, String page) {
    initializePageObject();
    iosSafariPage.enterText(TestDataLoader.getTestData(value), textfield, page);
  }

  @When("the user clicks button {string} on {string} page on " + CONTEXT)
  public void the_user_clicks_button_string_on_string_page_on_ios_safari_app(String button,
      String page) {
    initializePageObject();
    iosSafariPage.clickButton(button, page);
  }

  @When("the user open the url {string} on " + CONTEXT)
  public void the_user_open_the_url_string_on_ios_safari_app(String url) {
    initializePageObject();
    iosSafariPage.openURLIosSafari(TestDataLoader.getTestData(url));
  }

  @Then("the user sees button {string} in status {string} on " + CONTEXT)
  public void the_user_sees_button_string_in_status_string_on_ios_safari_app(String button,
      String status) {
    initializePageObject();
    iosSafariPage.checkButtonStatus(button, status);
  }

  @When("the user user close all tabs on " + CONTEXT)
  public void the_user_close_all_tabs_on_ios_safari_app() {
    initializePageObject();
    iosSafariPage.closeAllTab();
  }

  @Then("the user sees {string} {string} on {string} on " + CONTEXT)
  public void the_user_sees_string_string_on_string_on_ios_safari_app(String errorType,
      String errorText, String page) {
    initializePageObject();
    iosSafariPage.verifyErrorMessageCDP(errorType, TestDataLoader.getTestData(errorText), page);
  }

  @Then("the user sees does not see tetxfield {string} on " + CONTEXT)
  public void the_user_does_not_see_textfield_string_on_ios_safari_app(String textfield) {
    initializePageObject();
    iosSafariPage.verifyTextfieldNotVisible(textfield);
  }

  @And("the user sees element {string} on " + CONTEXT)
  public void the_user_sees_element_string_on_ios_safari_app(String element) {
    initializePageObject();
    iosSafariPage.verifyElementVissible(element);
  }

  @Then("the user sees message {string} on " + CONTEXT)
  public void the_user_sees_message_string_ios_safari_app(String message) {
    initializePageObject();
    iosSafariPage.verifyMessage(TestDataLoader.getTestData(message));
  }

  @Then("the user dont sees message {string} on " + CONTEXT)
  public void the_user_dont_sees_message_string_ios_safari_app(String message) {
    initializePageObject();
    iosSafariPage.verifyMessageNotVisible(TestDataLoader.getTestData(message));
  }

  @When("the user logs in as {string} and {string} on {string} page on " + CONTEXT)
  public void the_user_logs_in_as_string_and_string_on_string_page_on_ios_safari_app(
      String username, String pin, String page) {
    initializePageObject();
    iosSafariPage.enterText(TestDataLoader.getTestData(username), "cdp login email", page);
    iosSafariPage.enterText(TestDataLoader.getTestData(pin), "cdp login pin", page);
    iosSafariPage.clickButton("cdp login", page);
  }
}
