package pages.ios;

import config.TestDataLoader;
import io.appium.java_client.ios.IOSDriver;
import io.cucumber.core.exception.CucumberException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import locators.ios.IosSafariLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import steps.Hook;

public class IosSafariPage extends IosBasicPage {

  private static final Logger LOG = LogManager.getLogger(IosSafariPage.class);
  private final Map<String, String> xpathToButton = IosSafariLocators.createLabelLibraryButton();
  private final Map<String, String> xpathToTextfield = IosSafariLocators.createLabelLibraryTextfield();
  private final Map<String, String> xpathToUrl = IosSafariLocators.createLabelLibraryUrl();
  private final Map<String, String> xpathToTitle = IosSafariLocators.createLabelLibraryTitle();
  private final Map<String, String> xpathToElement = IosSafariLocators.createLabelLibraryElement();


  public IosSafariPage(IOSDriver driver) {
    super(driver);
  }

  public void removesafariData() {
    JavascriptExecutor js = driver;
    Map<String, Object> params = new HashMap<>();
    Map<Object, Object> scrollObject2 = new HashMap<>();

    // Launch Preferences
    launchApp("preferences");

    // Search Safari app on Preferences
    switchContext("native");
    params.put("direction", "down");
    js.executeScript("mobile: swipe", params);
    tapElementByXpath("//XCUIElementTypeSearchField[@name='Search']", false);
    waitForElementByXpath(30, 0, "//XCUIElementTypeSearchField[@name='Search']", false).sendKeys(
      "Safari");
    waitForElementByXpath(30, 1000,
      "//XCUIElementTypeCollectionView//XCUIElementTypeCell[@name='Safari']", true).click();

    // Search for Clear History and Website Data and clear history
    scrollObject2.put("predicateString", "value == 'Clear History and Website Data'");
    scrollObject2.put("direction", "down");
    js.executeScript("mobile: scroll", scrollObject2);

    waitForElementByXpath(30, 0, "//XCUIElementTypeStaticText[contains(@label,'Clear History')]",
      true).click();
    waitForElementByXpath(30, 0, "//XCUIElementTypeButton[@name='Clear History and Data']",
      true).click();
    //Wait for 10 seconds to ensure that clear action fully completed
    waitFor(10).seconds();
  }

  public void verifyLandingPageUrlTitle(String page, String url, String title) {
    setOrientation();
    cookiesPopUpsafariApp(5);
    String titleShown = "";
    LOG.info("using xpath: (//*[contains(@name,'{}')])[1]", xpathToTitle.get(title));
    int count = 0;
    while (count < 240) {
      List<WebElement> titles = driver.findElements(
        By.xpath("//*[contains(@name,'" + xpathToTitle.get(title) + "')]"));
      if (!titles.isEmpty()) {
        titleShown = titles.get(0).getAttribute("name");
        if (titleShown == null) {
          titleShown = titles.get(0).getAttribute("label");
        }
        if (titleShown != null) {
          break;
        }
      }
      count++;
      waitFor(250).milliseconds();
    }
    //  String titleShown = waitForElementByXpath(120, 500, "(//*[contains(@name,'" + xpathToTitle.get(title) + "')])[1]", false).getAttribute("name");
    LOG.info("title shown in safari app: {} vs. expected \"{}\" on \"{}\"", titleShown,
      xpathToTitle.get(title), page);

    waitForElementByXpath(120, 250, "(//XCUIElementTypeOther[@name='Address' or @name='URL'])[1]",
      true).click();
    String urlShown = waitForElementByXpath(30, 250, "//XCUIElementTypeTextField[@name='URL']",
      false).getAttribute("value");
    //   tapElementByXpath("//XCUIElementTypeButton[@name='Go']", true);
    LOG.info("url in safari app: \"{}\" vs. expected \"{}\" on \"{}\"", urlShown,
      xpathToUrl.get(url), page);

    assert titleShown != null;
    Assert.assertTrue(titleShown.contains(xpathToTitle.get(title)));
    Assert.assertTrue(urlShown.contains(xpathToUrl.get(url)));
  }

  public void enterText(String value, String textfield, String page) {
    setOrientation();
    if (textfield.equalsIgnoreCase("cdp login pin")) {
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp login pin1"), value.substring(0, 1));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp login pin2"), value.substring(1, 2));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp login pin3"), value.substring(2, 3));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp login pin4"), value.substring(3, 4));
    } else if (textfield.equalsIgnoreCase("cdp login email")) {
      tapElementByXpath("//XCUIElementTypeTextField[1]", true);
      typeTextIntoElementByXpath(xpathToTextfield.get(textfield), value);
    } else if (textfield.equalsIgnoreCase("cdp new pin")) {
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp new pin1"), value.substring(0, 1));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp new pin2"), value.substring(1, 2));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp new pin3"), value.substring(2, 3));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp new pin4"), value.substring(3, 4));
    } else if (textfield.equalsIgnoreCase("cdp confirm pin")) {
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp confirm pin1"), value.substring(0, 1));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp confirm pin2"), value.substring(1, 2));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp confirm pin3"), value.substring(2, 3));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp confirm pin4"), value.substring(3, 4));
    } else if (textfield.equalsIgnoreCase("set new pin")) {
      typeTextIntoElementByXpath(xpathToTextfield.get("set new pin1"), value.substring(0, 1));
      typeTextIntoElementByXpath(xpathToTextfield.get("set new pin2"), value.substring(1, 2));
      typeTextIntoElementByXpath(xpathToTextfield.get("set new pin3"), value.substring(2, 3));
      typeTextIntoElementByXpath(xpathToTextfield.get("set new pin4"), value.substring(3, 4));
    } else if (textfield.equalsIgnoreCase("confirm set new pin")) {
      typeTextIntoElementByXpath(xpathToTextfield.get("confirm set new pin1"),
        value.substring(0, 1));
      typeTextIntoElementByXpath(xpathToTextfield.get("confirm set new pin2"),
        value.substring(1, 2));
      typeTextIntoElementByXpath(xpathToTextfield.get("confirm set new pin3"),
        value.substring(2, 3));
      typeTextIntoElementByXpath(xpathToTextfield.get("confirm set new pin4"),
        value.substring(3, 4));
    } else if (textfield.equalsIgnoreCase("cdp old pin")) {
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp old pin1"), value.substring(0, 1));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp old pin2"), value.substring(1, 2));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp old pin3"), value.substring(2, 3));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp old pin4"), value.substring(3, 4));
    } else {
      typeTextIntoElementByXpath(xpathToTextfield.get(textfield), value);
    }
    if (textfield.contains("pin") || textfield.contains("Pin") || textfield.contains("asswort")) {
      LOG.info("enter ******** into textfield {} on {} page", textfield, page);
    } else {
      LOG.info("enter {} into textfield {} on {} page", value, textfield, page);
    }

  }

  public void clickButton(String button, String page) {
    setOrientation();
    closeKeyboard();
    assertAndVerifyElement(By.xpath(xpathToButton.get(button)));
    if ("close".equalsIgnoreCase(button)) {
      LOG.info(driver.findElements(By.xpath(xpathToButton.get(button))).size());
      waitForElementByXpath(360, 250, "//XCUIElementTypeButton[@name='ReloadButton']", true);
    } else {
      tapElementByXpath(xpathToButton.get(button), false);
    }
    LOG.info("clicks button {} on {} page", button, page);
    if (button.contains("save")) {
      waitFor(5).seconds();
    }
  }

  public void closeAllTab() {
    setOrientation();
    waitFor(500).seconds();
    tapElementByXpath("//XCUIElementTypeStaticText[@name='Cancel']", true);
    tapElementByXpath("//XCUIElementTypeButton[@name='TabsButton']", true);
    waitFor(500).seconds();
    int count = 1;
    List<WebElement> tabs = driver.findElements(
      By.xpath("//XCUIElementTypeButton[@name='closeTabButton']"));
    if (tabs.size() > 1) {
      while (count < tabs.size()) {
        tabs.get(0).click();
        count++;
        waitFor(500).milliseconds();
      }
      tapElementByXpath("//XCUIElementTypeButton[@name='TabViewDoneButton']", true);
      LOG.info("closed all tabs");
    }
  }

  public void openURLIosSafari(String url) {
    setOrientation();
    LOG.info("Opening link with the default browser: {}", url);
    HashMap<String, Object> args = new HashMap<>();
    if (Hook.browser.equals("safari")) {
      args.put("bundleId", "com.apple.mobilesafari");
    }
    LOG.info(args.get("bundleId"));
    driver.executeScript("mobile: launchApp", args);
    tapElementByXpath("//XCUIElementTypeButton[@name='URL']", true);
    typeTextIntoElementByXpath("//XCUIElementTypeTextField[@name='URL']", url);
    tapElementByXpath("//XCUIElementTypeButton[@name='Go']", true);

    waitFor(5).seconds();
    if (url.contains("skycdp") || url.contains("dein-sky.com")) {
      LOG.info("need to check for login to cdp");
      if (!driver.findElements(By.xpath(("//XCUIElementTypeTextField[@value='User Name']")))
        .isEmpty()) {
        typeTextIntoElementByXpath("//XCUIElementTypeTextField[@value='User Name']",
          TestDataLoader.getTestData("@TD:cdploginemail"));
        typeTextIntoElementByXpath("//XCUIElementTypeSecureTextField[@value='Password']",
          TestDataLoader.getTestData("@TD:cdploginpassword"));
        tapElementByXpath("//XCUIElementTypeButton[@name='Log In']", false);
      }
    }
    waitForElementByXpath(360, 250, "//XCUIElementTypeButton[@name='ReloadButton']", true);
    cookiesPopUpsafariApp(5);
  }

  public void checkButtonStatus(String button, String status) {
    setOrientation();
    closeKeyboard();
    String locator = xpathToButton.get(button);
    WebElement webElement = driver.findElement(By.xpath(locator));
    String statusButton = webElement.getAttribute("enabled");
    LOG.info("status button: \"{}\"", statusButton);
    switch (status) {
      case "enabled":
        Assert.assertEquals("true", statusButton);
        break;
      case "disabled":
        Assert.assertEquals("false", statusButton);
        break;
      default:
        throw new CucumberException("please choose a valid status: enabled or disabled");
    }
  }

  public void verifyErrorMessageCDP(String errorType, String errorText, String page) {
    setOrientation();
    closeKeyboard();
    waitFor(1).seconds();
    String locator = xpathToTextfield.get(errorType);
    WebElement error = waitForElementByXpath(30, 250, locator, false);
    String shownText = error.getAttribute("label");
    if (shownText == null) {
      shownText = error.getAttribute("value");
      if (shownText == null) {
        shownText = error.getAttribute("name");
      }
    }
    LOG.info("expected: \"{}\" vs. shown: \"{}\"", errorText, shownText);
    Assert.assertTrue(shownText.contains(errorText));
    LOG.info("found expected error message on {}", page);
  }

  public void verifyTextfieldNotVisible(String textfield) {
    if (textfield.equalsIgnoreCase("set new pin")) {
      Assert.assertTrue(
        driver.findElements(By.xpath(xpathToTextfield.get("set new pin1"))).isEmpty());
      Assert.assertTrue(
        driver.findElements(By.xpath(xpathToTextfield.get("set new pin2"))).isEmpty());
      Assert.assertTrue(
        driver.findElements(By.xpath(xpathToTextfield.get("set new pin3"))).isEmpty());
      Assert.assertTrue(
        driver.findElements(By.xpath(xpathToTextfield.get("set new pin4"))).isEmpty());
    } else if (textfield.equalsIgnoreCase("confirm set new pin")) {
      Assert.assertTrue(
        driver.findElements(By.xpath(xpathToTextfield.get("confirm set new pin1"))).isEmpty());
      Assert.assertTrue(
        driver.findElements(By.xpath(xpathToTextfield.get("confirm set new pin2"))).isEmpty());
      Assert.assertTrue(
        driver.findElements(By.xpath(xpathToTextfield.get("confirm set new pin3"))).isEmpty());
      Assert.assertTrue(
        driver.findElements(By.xpath(xpathToTextfield.get("confirm set new pin4"))).isEmpty());
    } else {
      Assert.assertTrue(driver.findElements(By.xpath(xpathToTextfield.get(textfield))).isEmpty());
    }
  }

  public void verifyElementVissible(String element) {
    closeKeyboard();
    Assert.assertTrue(
      waitForElementByXpath(60, 150, xpathToElement.get(element), false).isDisplayed());
  }

  public void verifyMessage(String message) {
    closeKeyboard();
    String locator = "//*[contains(@value,'" + message + "') or contains(@label,'" + message
      + "') or contains(@name,'" + message + "')]";
    Assert.assertTrue(waitForElementByXpath(60, 250, locator, false).isDisplayed());
  }

  public void verifyMessageNotVisible(String message) {
    closeKeyboard();
    String locator = "//*[contains(@value,'" + message + "') or contains(@label,'" + message
      + "') or contains(@name,'" + message + "')]";
    Assert.assertTrue(driver.findElements(By.xpath(locator)).isEmpty());
  }
}
