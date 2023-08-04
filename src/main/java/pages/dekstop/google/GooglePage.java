package pages.dekstop.google;

import config.BasePage;
import config.DriverUtil;
import core.ProcessElement;
import java.util.Map;
import locators.desktop.GoogleLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.RemoteWebDriver;

public class GooglePage extends BasePage {

  public ProcessElement processElement;

  public GooglePage(RemoteWebDriver driver) {
    super(driver);
  }

  private static final Logger LOG = LogManager.getLogger(GooglePage.class);
  private final Map<String, String> xpathToInput = GoogleLocators.createLibraryInput();

  public void enterData(String data, String element) {
    waitForPageLoaded();
    String locator = xpathToInput.get(element);
    enterText(data, locator, locator);
  }

  public void openUrl(String url) {
    DriverUtil.threadLocalActiveBrowsers.get().get("current").get(url);
  }
}
