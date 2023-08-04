package steps.desktop.google;

import config.DriverUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import pages.dekstop.google.GooglePage;

public class GoogleSteps {

  private GooglePage page;

  public GoogleSteps() {
    page = new GooglePage(DriverUtil.getDriver());
  }

  @When("the user enters {string} into {string} input")
  public void enterCorrect(String info, String input){
    page.enterData(info, input);
  }
  @Given("the user can open the link {string}")
  public void the_user_can_open_string(String url) {
    page.openUrl(url);
  }

}
