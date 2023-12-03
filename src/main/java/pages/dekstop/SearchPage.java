package pages.dekstop;

import config.BasePage;
import config.DriverUtil;
import core.ProcessElement;
import java.util.Map;
import locators.desktop.SearchLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.asserts.Assertion;

public class SearchPage extends BasePage {

  public ProcessElement processElement;

  public SearchPage(RemoteWebDriver driver) {
    super(driver);
  }

  private static final Logger LOG = LogManager.getLogger(SearchPage.class);
  private final Map<String, String> xpathToInput = SearchLocators.createLibraryInput();

  public void enterData(String data, String element) {
    waitForPageLoaded();
    String locator = xpathToInput.get(element);
    enterText(data, locator, locator);
  }

  public void openUrl(String url) {
    DriverUtil.threadLocalActiveBrowsers.get().get("current").get(url);
  }

  public void verifyTitlePage(String title) {
    waitForPageLoaded();
    boolean status = DriverUtil.threadLocalActiveBrowsers.get().get("current").getTitle().contains(title);
    Assert.assertTrue(status,"Wrong page !");
  }
}
