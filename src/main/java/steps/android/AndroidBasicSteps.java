package steps.android;

import config.DriverUtil;
import io.cucumber.java.en.When;
import pages.android.AndroidBasicPage;

public class AndroidBasicSteps {

  private AndroidBasicPage androidBasicPage;
  private AndroidBasicSteps() {
    androidBasicPage = new AndroidBasicPage(DriverUtil.getAndroidDriver());
  }

  @When("running test with user {string}")
  public void running_test_with_user(String user) {
    androidBasicPage.runWith(user);
  }

  @When("the user launches {string} app on the mobile device")
  public void the_user_launches_string_app_on_the_mobile_device(String appName) {
    androidBasicPage.launchApp(appName);
  }

  @When("the user switches to app {string}")
  public void the_user_switches_back_to_app_string(String app) {
    androidBasicPage.switchApp(app);
  }

  @When("the user closes all running apps")
  public void the_user_closes_all_running_apps() {
    androidBasicPage.closeAllRunningApps();
  }
}
