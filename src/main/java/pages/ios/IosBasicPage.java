package pages.ios;

import config.DriverUtil;
import config.TestDataLoader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import io.cucumber.core.exception.CucumberException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import steps.Hook;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.time.Duration.ofMillis;

public class IosBasicPage {

  public final IOSDriver driver;
  private static final Logger LOG = LogManager.getLogger(IosBasicPage.class);
  private static final String MAC_IP_ADDRESS = "192.168.178.39";
  public static String browser = Hook.browser;

  public IosBasicPage(IOSDriver driver) {
    this.driver = driver;
  }

  public static int adbCommand(String command) {
    CommandLine cmd = CommandLine.parse(
        "ssh pascalkallenborn@" + MAC_IP_ADDRESS + " \"" + command + "\"");
    LOG.info("used string to connect: {}", cmd);
    DefaultExecutor shell = new DefaultExecutor();
    int exitValue = 9999;
    //Delete app cache and data
    try {
      exitValue = shell.execute(cmd);
      return exitValue;
    } catch (IOException ignored) {
    }
    return exitValue;
  }

  public void openURL(String url) {
    setOrientation();
    switchContext("native");
    waitFor(1).seconds();
    LOG.info("Opening link with the default browser: {}", url);
    // Enter URL
    switch (browser) {
      case "safari":
        tapElementByXpath("//XCUIElementTypeButton[@name='URL']", true);
        typeTextIntoElementByXpath("//XCUIElementTypeTextField[@name='URL']", url);
        break;
      case "chrome":
      case "firefox":
        break;
      default:
        LOG.info("No need additional actions. Continue...");
    }
    tapElementByXpath("//XCUIElementTypeButton[@name='Go']", true);

    if (url.contains("skycdp") || url.contains("dein-sky.com")) {
      LOG.info("need to check for login to cdp");
      try {
        typeTextIntoElementByXpath("//XCUIElementTypeTextField[@value='User Name']",
            TestDataLoader.getTestData("@TD:cdploginemail"));
        typeTextIntoElementByXpath("//XCUIElementTypeSecureTextField[@value='Password']",
            TestDataLoader.getTestData("@TD:cdploginpassword"));
        tapElementByXpath("//XCUIElementTypeButton[@name='Log In']", false);
      } catch (Exception ignored) {
        LOG.info("login to cdp is not needed");
      }
//            if (waitForElementByXpath(10, 250, "//XCUIElementTypeTextField[@value='User Name']", false).isDisplayed()) {
//                typeTextIntoElementByXpath("//XCUIElementTypeTextField[@value='User Name']", TestDataLoader.getTestData("@TD:cdploginemail"));
//                typeTextIntoElementByXpath("//XCUIElementTypeSecureTextField[@value='Password']", TestDataLoader.getTestData("@TD:cdploginpassword"));
//                tapElementByXpath("//XCUIElementTypeButton[@name='Log In']", false);
//            }
    }
    waitForElementByXpath(30, 250, "//XCUIElementTypeButton[@name='ReloadButton']", true);
    cookiesPopUpsafariApp(5);
  }

  public WebElement waitForElementByXpath(long timeOutInSeconds, long sleepInMillis,
      String elementXpath, boolean clickable) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds),
        ofMillis(sleepInMillis));
    if (clickable) {
      return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(elementXpath)));
    } else {
      return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath)));
    }
  }

  public WebElement waitForElementByAccessibilityId(long timeOutInSeconds, long sleepInMillis,
      String elementAccessibilityId, boolean clickable) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds),
        ofMillis(sleepInMillis));
    if (clickable) {
      return wait.until(ExpectedConditions.elementToBeClickable(
          AppiumBy.accessibilityId(elementAccessibilityId)));
    } else {
      return wait.until(ExpectedConditions.visibilityOfElementLocated(
          AppiumBy.accessibilityId(elementAccessibilityId)));
    }
  }

  public WebElement waitForElementById(long timeOutInSeconds, long sleepInMillis, String elementId,
      boolean clickable) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds),
        ofMillis(sleepInMillis));
    if (clickable) {
      return wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id(elementId)));
    } else {
      return wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id(elementId)));
    }
  }

  public List<WebElement> waitForPresenceOfElementsLocatedByXpath(long timeOutInSeconds,
      long sleepInMillis, String elementXpath) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds),
        ofMillis(sleepInMillis));
    return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(elementXpath)));
  }

  public void waitForInvisibilityOfElement(long timeOutInSeconds, String elementXpath) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(elementXpath)));
  }

  public void tapElementByXpath(String elementXpath, Boolean clickable) {
    waitForElementByXpath(30, 250, elementXpath, clickable).click();
    LOG.info("Clicked element located by {}", elementXpath);
  }

  public void tapElementByAccessibilityId(String elementAccessibilityId) {
    waitForElementByAccessibilityId(30, 250, elementAccessibilityId, true).click();
  }

  public void tapElementById(String elementId) {
    waitForElementById(60, 250, elementId, true).click();
  }

  public void typeTextIntoElementByXpath(String elementXpath, String text) {
    waitForElementByXpath(30, 250, elementXpath, false).clear();
    waitForElementByXpath(30, 250, elementXpath, false).sendKeys(text);
    closeKeyboard();
    LOG.info("Value \"{}\" was inputted into element located by {}", text, elementXpath);
  }

  public void typeTextIntoElementByAccessibilityId(String elementAccessibilityId, String text) {
    waitForElementByAccessibilityId(30, 250, elementAccessibilityId, true).sendKeys(text);
  }

  public void typeTextIntoElementById(String elementId, String text) {
    waitForElementById(30, 250, elementId, true).sendKeys(text);
  }

  public void runWith(String user) {
    DriverUtil.getDeviceCapabilities(user);
    DriverUtil.initIosDriver();
  }

  public void launchApp(String appName) {
    closeApp(appName);
    String appPackage;
    switch (appName.toLowerCase()) {
      case "messenger":
        appPackage = "com.facebook.Messenger";
        break;
      case "whatsapp":
        appPackage = "net.whatsapp.WhatsApp";
        break;
      case "skyapp":
        appPackage = "com.";
        break;
      case "sms":
        appPackage = "com.apple.MobileSMS";
        break;
      case "preferences":
        appPackage = "com.apple.Preferences";
        break;
      case "safari":
        appPackage = "com.apple.mobilesafari";
        break;
      case "chrome":
        appPackage = "com.google.chrome.ios";
        break;
      case "firefox":
        appPackage = "com.apple.mobilefirefox";
        break;
      default:
        throw new CucumberException("please chose a valid App to run!");
    }
    HashMap<String, Object> args = new HashMap<>();
    args.put("bundleId", appPackage);
    driver.executeScript("mobile: launchApp", args);
    LOG.info("Launched {} successfully", appName);
    if (appName.equalsIgnoreCase("safari")) {
      waitFor(3).seconds();
      waitForBothNativeAndWebViewContextAvailable(6, 3);
      switchContext("webview");
    }
  }

  public void switchApp(String app) {
    String appPackage = "";
    switch (app.toLowerCase()) {
      case "messenger":
        appPackage = "com.facebook.Messenger";
        break;
      case "whatsapp":
        appPackage = "net.whatsapp.WhatsApp";
        break;
      case "safari":
        appPackage = "com.apple.mobilesafari";
        break;
      case "sms":
        appPackage = "com.apple.MobileSMS";
        break;
      default:
        throw new CucumberException("Invalid app name!");
    }
    driver.activateApp(appPackage);
    //Allow a little delay for the phone to switch the app
    waitFor(1).seconds();
  }

  public void closeApp(String app) {
    String appPackage;
    switch (app.toLowerCase()) {
      case "messenger":
        appPackage = "com.facebook.Messenger";
        break;
      case "whatsapp":
        appPackage = "net.whatsapp.WhatsApp";
        break;
      case "skyapp":
        appPackage = "com.";
        break;
      case "sms":
        appPackage = "com.apple.MobileSMS";
        break;
      case "preferences":
        appPackage = "com.apple.Preferences";
        break;
      case "safari":
        appPackage = "com.apple.mobilesafari";
        break;
      case "chrome":
        appPackage = "com.google.chrome.ios";
        break;
      case "firefox":
        appPackage = "com.apple.mobilefirefox";
        break;
      default:
        throw new CucumberException("please chose a valid App to run!");
    }
    if (!app.equalsIgnoreCase("safari")) {
      HashMap<String, Object> args = new HashMap<>();
      JavascriptExecutor js = driver;
      args.put("bundleId", appPackage);
      js.executeScript("mobile: launchApp", args);
      js.executeScript("mobile: terminateApp", args);
      LOG.info("Terminated {} successfully", app);
    }
  }

  public void setOrientation() {
    // DriverUtil.getIosDriver().rotate(ScreenOrientation.PORTRAIT);
  }

  public void cookiesPopUpsafariApp(long seconds) {
    try {
      LOG.info("checking for cookie popup");
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
      wait.until(ExpectedConditions.visibilityOfElementLocated(
          By.xpath("//*[contains(@title,'akzeptieren') or contains(@label,'accept')]")));
      tapElementByXpath("//*[contains(@title,'akzeptieren') or contains(@label,'accept')]", true);
      LOG.info("cookies accepted");
    } catch (Exception ignored) {
    }
  }

  public void assertAndVerifyElement(By element) {
    boolean isPresent = false;
    for (int i = 0; i < 1000; i++) {
      try {
        if (driver.findElement(element) != null) {
          isPresent = true;
          break;
        }
      } catch (Exception ignored) {
        waitFor(1).seconds();
      }
    }
    Assert.assertTrue(isPresent);
  }

  public void closeKeyboard() {
    String locator = "//XCUIElementTypeToolbar[@name='Toolbar']//XCUIElementTypeButton[@name='Done']";
    if (!driver.findElements(By.xpath(locator)).isEmpty()) {
      driver.findElement(By.xpath(locator)).click();
      LOG.info("Keyboard closed");
    } else {
      LOG.info("Keyboard not display");
    }
  }

  public void waitForPageLoaded() {
    // In case that Save Password panel appear
    switchContext("native");
    if (!driver.findElements(
            By.xpath("//XCUIElementTypeSheet[contains(@name,'Would you like to save this password')]"))
        .isEmpty()) {
      tapElementByXpath("//XCUIElementTypeButton[@name='Never for This Website']", false);
    }

    // Waiting for Loading indicator to disappear
    switchContext("webview");
    for (int i = 0; i < 120; i++) {
      if (!driver.findElements(By.xpath("//*[@data-testid='spinner']")).isEmpty()) {
        LOG.info("Page loading...");
      } else {
        LOG.info("Page loaded successfully");
        break;
      }
      waitFor(1).seconds();
      if (i == 119) {
        LOG.info("Page loaded too long! Stop waiting!");
      }
    }
  }

  public void scrollObject(String locatorName, String locator) {
    try {
      String direction = "down";
      while (!driver.findElement(By.xpath(locator)).isDisplayed()) {
        if (driver.findElement(By.xpath("//XCUIElementTypeOther[@name='content information']"))
            .isDisplayed()) {
          direction = "up";
        }
        scroll(direction);
      }
      LOG.info("Scrolled to locator {} successfully", locatorName);
    } catch (Exception e) {
      LOG.info("No element found!");
      throw e;
    }
  }

  public void scrollToTop() {
    scrollObject("sky logo", "(//XCUIElementTypeImage[@name='Sky'])[1]");
    LOG.info("Scrolled to top of page successfully");
  }

  protected void scroll(String direction) {
    //if pressX was zero it didn't work for me
    int pressX = driver.manage().window().getSize().width / 2;
    // 1/7 of the screen as the bottom finger-press point
    int topY = driver.manage().window().getSize().height / 6;
    // 3/5 of the screen as the bottom finger-press point
    int bottomY = driver.manage().window().getSize().height * 3 / 5;
    LOG.info("Bottom Y: {}", bottomY);
    //scroll with TouchAction by itself
    //06/16/2023 - TL: Commented out for now because it is deprecated. Need to find a better solution for TouchAction in case needed
//        TouchAction touchAction = new TouchAction(driver);
//        if (direction.equals("up"))
//            touchAction.longPress(PointOption.point(pressX, topY)).moveTo(PointOption.point(pressX, bottomY)).release().perform();
//        else if (direction.equals("down"))
//            touchAction.longPress(PointOption.point(pressX, bottomY)).moveTo(PointOption.point(pressX, topY)).release().perform();
  }

  public void switchContext(String ct) {
    String currentContext = driver.getContext();
    Set<String> contexts = driver.getContextHandles();
    LOG.info("Current context: {}", currentContext);
    assert currentContext != null;
    if (!currentContext.toLowerCase().contains(ct)) {
      for (String context : contexts) {
        if (context.toLowerCase().contains(ct.toLowerCase())) {
          driver.context(context);
          LOG.info("Switch context to: {}", context);
          break;
        }
      }
    }
  }

  public void verifyInformationDisplay(String informationType, List<String> infoList) {
    infoList.forEach(info -> {
      if (informationType.equals("message")) {
        String msgXpath = String.format("//*[@value=\"%s\"]", TestDataLoader.getTestData(info));
        waitForElementByXpath(60, 500, msgXpath, false);
      }
    });
  }

  public WebElement waitForPresenceOfElementByXpath(int timeout, String elementXpath) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(elementXpath)));
  }

  public void tapByCoordinates(int x, int y) {
    Map<String, Object> args = new HashMap<>();
    args.put("x", x);
    args.put("y", y);
    driver.executeScript("mobile: tap", args);
    LOG.info("Tapped on the coordinate at x = {} and y = {}", x, y);
  }

  public void selectPickerWheelOfElement(WebElement webElement, String order, double offset) {
    Map<String, Object> params = new HashMap<>();
    params.put("order", order);
    params.put("offset", offset);
    params.put("elementId", ((RemoteWebElement) webElement).getId());
    driver.executeScript("mobile: selectPickerWheelValue", params);
    LOG.info("Select picker wheel with order {} and offset {}", order, offset);
  }

  public void swipeOnElementToDirection(WebElement webElement, String direction, int velocity) {
    Map<String, Object> params = new HashMap<>();
    params.put("direction", direction);
    params.put("velocity", velocity);
    params.put("elementId", ((RemoteWebElement) webElement).getId());
    driver.executeScript("mobile: swipe", params);
    LOG.info("Swipe {} with velocity {}", direction, velocity);
  }

  public void closeTabAtIndex(int tabIndex) {
    openTiltedTabView();
    String allOpenedTabXpath = "//XCUIElementTypeButton[@name=\"closeTabButton\"]";
    int allOpenedTabs = driver.findElements(By.xpath(allOpenedTabXpath)).size();
    while (driver.findElements(By.xpath(allOpenedTabXpath)).size() >= allOpenedTabs) {
      String tabCloseIconXpath = String.format(
          "(//XCUIElementTypeButton[@name=\"closeTabButton\"])[%s]", tabIndex);
      tapElementByXpath(tabCloseIconXpath, false);
      LOG.info("Closed tab at index {}", tabIndex);
      //Wait about 1 seconds until the tab flies out
      waitFor(1).seconds();
    }
  }

  public void switchToTabAtIndex(int tabIndex) {
    openTiltedTabView();
    String expectedHandleTabXpath = String.format(
        "(//XCUIElementTypeButton[@name=\"TiltedTabThumbnailView\"])[%s]", tabIndex);
    tapElementByXpath(expectedHandleTabXpath, false);
    waitForElementByAccessibilityId(5, 1000L, "TabsButton", false);
  }

  private void openTiltedTabView() {
    try {
      waitForElementByAccessibilityId(2, 1000L, "AddTabButton", false);
      LOG.info("Tilted Tab View already opened!");
    } catch (Exception e) {
      LOG.info("Opening Tilted Tab View....");
      tapElementByAccessibilityId("TabsButton");
      waitForElementByAccessibilityId(5, 1000L, "AddTabButton", false);
      LOG.info("Tilted Tab View opened successfully!");
    }
  }

  public WaitBuilder waitFor(int duration) {
    return new WaitBuilder(duration);
  }

  public static class WaitBuilder {

    private final int duration;

    public WaitBuilder(int duration) {
      this.duration = duration;
    }

    public void seconds() {
      try {
        Thread.sleep(duration * 1000L);
        LOG.info("Wait for {} seconds", duration);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }

    public void minutes() {
      try {
        Thread.sleep(duration * 1000L * 60);
        LOG.info("Wait for {} minutes", duration);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }

    public void milliseconds() {
      try {
        Thread.sleep(duration);
        LOG.info("Wait for {} milliseconds", duration);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  public void waitForBothNativeAndWebViewContextAvailable(int retryCount,
      int waitIntervalInSeconds) {
    int retry = 0;
    while (driver.getContextHandles().size() < 2) {
      LOG.info("The WEB VIEW context is not ready yet. Try again");
      waitFor(waitIntervalInSeconds).seconds();
      retry++;
      if (retry >= retryCount) {
        throw new CucumberException(
            "Web view context not loaded after " + waitIntervalInSeconds * retryCount + "seconds");
      }
    }
  }

  public void scrollToElement(String expectedElementXpathToVisible, String elementXpathToStop,
      String direction, int velocity) {
    boolean elementIsFound = false;
    int retry = 0;
    do {
      try {
        waitForElementByXpath(2, 500, expectedElementXpathToVisible, false);
        elementIsFound = true;
        LOG.info("Element with xpath {} found", expectedElementXpathToVisible);
        retry = 50;
      } catch (Exception e) {
        LOG.info("Element with xpath {} not visible in current screen!",
            expectedElementXpathToVisible);
        swipeScreenToDirection(direction, velocity);
        try {
          waitForElementByXpath(1, 500, elementXpathToStop, false);
          if (expectedElementXpathToVisible.equals(elementXpathToStop)) {
            elementIsFound = true;
            LOG.info("Element with xpath {} found", expectedElementXpathToVisible);
          }
          break;
        } catch (Exception ex) {
          LOG.info("Element to stop scroll not reached. Continue...");
        }
        retry++;
      }
    } while (retry <= 50);
    if (!elementIsFound) {
      throw new CucumberException(
          "Element with xpath " + expectedElementXpathToVisible + " not found");
    }
  }

  private void swipeScreenToDirection(String direction, int velocity) {
    Map<String, Object> params = new HashMap<>();
    params.put("direction", direction);
    params.put("velocity", velocity);
    driver.executeScript("mobile: swipe", params);
    LOG.info("Swipe {} with velocity {}", direction, velocity);
  }

  public void handleNewWindowPopupInSafari() {
    if (Hook.browser.equalsIgnoreCase("safari") && Hook.platform.equals("ios-webApp")) {
      try {
        LOG.info("Switch to context native app to handle authorization popup");
        switchContext("native");
        String allowButtonXpath = "//XCUIElementTypeButton[@name='Allow']";
        tapElementByXpath(allowButtonXpath, false);
        waitForInvisibilityOfElement(15, allowButtonXpath);
        LOG.info("Switch to context web view to continue the test...");
        switchContext("webview");
      } catch (Exception e) {
        throw new CucumberException(
            "Handle authorization popup is failed due to: " + e.getMessage());
      }
    }
  }
}
