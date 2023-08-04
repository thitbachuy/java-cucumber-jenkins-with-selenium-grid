package steps.android;

import config.DriverUtil;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import pages.android.AndroidChromePage;
import steps.Hook;

public class AndroidChromeSteps {

  private static AndroidChromePage androidChromePage;
  public static final String CONTEXT = "android chrome app";

  private AndroidChromeSteps() {
    androidChromePage = new AndroidChromePage(DriverUtil.getAndroidDriver());
  }

  @Given("the user has removed existing data of " + CONTEXT)
  public void the_user_has_removed_existing_data_of_android_chrome_app() {
    switch (Hook.platform) {
      case "android-nativeApp":
        androidChromePage.removeChromeData();
        break;
      case "android-ssh":
        androidChromePage.removeChromeDataSSH();
        break;
      default:
        throw new CucumberException("please choose a valid android option");
    }
  }

  /**
   * Examples:  page = sky contract page, url = contract help url, title = contract help title
   */
  @Then("the user sees page {string} with url {string} and title {string} on " + CONTEXT)
  public void the_user_sees_page_string_with_url_string_and_title_string_on_android_chrome_app(
      String page, String url, String title) {
    androidChromePage.verifyLandingPageUrlTitle(page, url, title);
  }

}
