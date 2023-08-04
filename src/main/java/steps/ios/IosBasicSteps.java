package steps.ios;

import config.DriverUtil;
import config.TestDataLoader;
import io.cucumber.java.en.When;
import pages.ios.IosBasicPage;
import pages.ios.IosSafariPage;
import steps.Hook;

public class IosBasicSteps {

  private IosBasicPage iosBasicPage;
  private IosSafariPage iosSafariPage;

  private IosBasicSteps() {
    iosBasicPage = new IosBasicPage(DriverUtil.getIosDriver());
  }

  @When("running test with user {string} on ios mobile device")
  public void running_test_with_user_string_on_ios_mobile_device(String user) {
    iosBasicPage.runWith(user);
  }

  @When("the user launches {string} app on ios mobile device")
  public void the_user_launches_string_app_on_ios_mobile_device(String appName) {
    if (appName.equals("test browser")) {
      appName = Hook.browser;
      switch (appName) {
        case "safari":
          iosSafariPage.removesafariData();
          break;
        case "chrome":
        case "firefox":
          break;
      }
    }
    iosBasicPage.launchApp(appName);
  }

  @When("the user close app {string} on ios mobile device")
  public void the_user_close_app_string_on_ios_mobile_device(String app) {
    iosBasicPage.closeApp(app);
  }

  @When("the user switch to app {string} on ios mobile device")
  public void the_user_switch_back_to_app_string_on_ios_mobile_device(String app) {
    iosBasicPage.switchApp(app);
  }

  @When("the user open the url {string} on ios mobile device")
  public void the_user_open_the_url_string_on_ios_safari_app(String url) {
    iosBasicPage.openURL(TestDataLoader.getTestData(url));
  }
}
