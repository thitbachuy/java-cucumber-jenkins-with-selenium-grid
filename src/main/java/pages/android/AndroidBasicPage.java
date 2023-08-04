package pages.android;

import config.DriverUtil;
import config.TestDataLoader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.cucumber.core.exception.CucumberException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import locators.android.AndroidChromeLocators;
import locators.android.DatePickerLocators;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import steps.Hook;

public class AndroidBasicPage {

  public final AndroidDriver driver;
  private static final Logger LOG = LogManager.getLogger(AndroidBasicPage.class);
  private static final String MAC_IP_ADDRESS = "192.168.178.39";

  public AndroidBasicPage(AndroidDriver driver) {
    this.driver = driver;
  }

  public WebElement waitForElementByXpath(long timeOutInSeconds, long sleepInMillis,
      String elementXpath, boolean clickable) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds),
        Duration.ofMillis(sleepInMillis));
    if (clickable) {
      return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(elementXpath)));
    } else {
      return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath)));
    }
  }

  public WebElement waitForElementByAccessibilityId(long timeOutInSeconds, long sleepInMillis,
      String elementAccessibilityId, boolean clickable) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds),
        Duration.ofMillis(sleepInMillis));
    if (clickable) {
      return wait.until(ExpectedConditions.elementToBeClickable(
          new AppiumBy.ByAccessibilityId(elementAccessibilityId)));
    } else {
      return wait.until(ExpectedConditions.visibilityOfElementLocated(
          new AppiumBy.ByAccessibilityId(elementAccessibilityId)));
    }
  }

  public WebElement waitForElementById(long timeOutInSeconds, long sleepInMillis, String elementId,
      boolean clickable) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds),
        Duration.ofMillis(sleepInMillis));
    if (clickable) {
      return wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id(elementId)));
    } else {
      return wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id(elementId)));
    }
  }

  public void tapElementByXpath(String elementXpath) {
    waitForElementByXpath(60, 500, elementXpath, true).click();
  }

  public void tapElementByAccessibilityId(String elementAccessibilityId) {
    waitForElementByAccessibilityId(60, 500, elementAccessibilityId, true).click();
  }

  public void tapElementById(String elementId) {
    waitForElementById(60, 500, elementId, true).click();
  }

  public void typeTextIntoElementByXpath(String elementXpath, String text) {
    waitForElementByXpath(15, 500, elementXpath, true).clear();
    waitForElementByXpath(1, 500, elementXpath, true).sendKeys(text);
  }

  public void typeTextIntoElementById(String elementId, String text) {
    waitForElementById(30, 500, elementId, true).sendKeys(text);
  }

  public void launchApp(String appName) {
    String appPackage = "", appActivity = "", appWaitPackage = "", appWaitActivity = "";
    switch (appName) {
      case "Messenger Lite":
        appPackage = "com.facebook.mlite";
        appActivity = ".coreui.view.MainActivity";
        appWaitPackage = "com.facebook.mlite";
        appWaitActivity = ".sso.view.LoginActivity";
        break;
      case "Whatsapp":
        appPackage = "com.whatsapp";
        appActivity = ".HomeActivity";
        appWaitPackage = "com.whatsapp";
        appWaitActivity = ".HomeActivity";
        break;
      case "SkyApp":
        appPackage = "com.";
        appActivity = "";
        appWaitPackage = "com.";
        appWaitActivity = "";
        break;
      case "SMS":
        appPackage = "com.p1.chompsms";
        appActivity = ".activities.MainActivity";
        appWaitPackage = "com.p1.chompsms";
        appWaitActivity = ".activities.MainActivity";
        break;
      case "Facebook":
        appPackage = "com.facebook.katana";
        appActivity = ".LoginActivity";
        appWaitPackage = "com.facebook.katana";
        appWaitActivity = ".LoginActivity";
        break;
      case "Twitter":
        appPackage = "com.twitter.android";
        appActivity = ".StartActivity";
        appWaitPackage = "com.twitter.android";
        appWaitActivity = ".StartActivity";
        break;
      case "test browser":
        if (Hook.browser.equals("chrome") || Hook.browser.equals("chromeHeadless")) {
          //Initialization
          clearDataOfApplication("chrome");
          appPackage = "com.android.chrome";
          appActivity = "com.google.android.apps.chrome.Main";
          appWaitPackage = "com.android.chrome";
          appWaitActivity = "*.chrome.*";
        }
        break;
      default:
        throw new CucumberException("please chose a valid App to run!");
    }
    Activity activity = new Activity(appPackage, appActivity);
    startAppActivity(activity, appWaitPackage, appWaitActivity);
    String locatorBackUp = "//android.widget.TextView[@text='Google Drive backup']";
    if (!driver.findElements(By.xpath(locatorBackUp)).isEmpty()) {
      tapElementByXpath("//android.widget.RadioButton[@text='Never']");
      tapElementByXpath(
          "//android.widget.Button[@text='DONE' or @resource-id='com.whatsapp:id/gdrive_new_user_setup_btn']");
    }
    finishTourGuideOfApplication(appName);
  }

  public void startAppActivity(Activity activity, String appWaitPackage, String appWaitActivity) {
    activity.setAppWaitPackage(appWaitPackage);
    activity.setAppWaitActivity(appWaitActivity);
    driver.startActivity(activity);
  }

  //Finish app tour guide
  private void finishTourGuideOfApplication(String appName) {
    if (appName.equals("test browser")) {
      if (Hook.browser.equals("chrome") || Hook.browser.equals("chromeHeadless")) {
        finishTourGuideOfChrome();
      } else if (Hook.browser.equals("firefox")) {

      }
    }
  }

  private void finishTourGuideOfChrome() {
    //Accept terms and conditions
    try {
      tapElementById("com.android.chrome:id/send_report_checkbox");
      tapElementById("com.android.chrome:id/terms_accept");
    } catch (Exception e) {
      LOG.info("Terms and conditions not appear");
    }
    //Decline the sync option (if prompted)
    try {
      waitForElementById(3, 500, "com.android.chrome:id/negative_button", false).click();
    } catch (TimeoutException ignored) {
      LOG.info("Sync option not prompted. Continue...");
    }
    //Disable password saving and auto-translate suggestions
    tapElementByAccessibilityId("More options");
    tapElementByAccessibilityId("Settings");
    tapElementByXpath("//android.widget.TextView[@text='Passwords']");
    tapElementById("com.android.chrome:id/switchWidget");
    tapElementByAccessibilityId("Navigate up");
    waitForElementByXpath(30, 2000,
        AndroidChromeLocators.createLabelLibrary().get("settingsMenuFirstElement"), false);
    driver.findElement(new AppiumBy.ByAndroidUIAutomator(
            "new UiScrollable(new UiSelector().resourceId(\"com.android.chrome:id/recycler_view\")).scrollIntoView(new UiSelector().text(\"Languages\"));"))
        .click();
    tapElementById("com.android.chrome:id/switchWidget");
    tapElementByAccessibilityId("Navigate up");
    tapElementByAccessibilityId("Navigate up");
  }

  //Clear cache, data of application
  private void clearDataOfApplication(String application) {
    String appPackage;
    if ("chrome".equals(application)) {
      appPackage = "com.android.chrome";
    } else {
      throw new CucumberException("Application " + application + " not supported");
    }
    CommandLine cmdClear = CommandLine.parse(
        "adb -s " + System.getProperty("deviceUDID") + " shell pm clear " + appPackage);
    DefaultExecutor shell = new DefaultExecutor();
    int exitValue = 9999;
    //Delete app cache and data
    try {
      exitValue = shell.execute(cmdClear);
    } catch (IOException ignored) {
    }
    if (exitValue > 0) {
      throw new CucumberException("Cannot remove " + application + " app data!");
    }
  }

  public void cookiesPopUpChromeApp(long seconds) {
    try {
      LOG.info("checking for cookie popup");
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
      wait.until(ExpectedConditions.visibilityOfElementLocated(
          By.xpath("//android.view.View[contains(@resource-id,'sp_message_iframe')]")));
      tapElementByXpath("//android.widget.Button[@text='Alle akzeptieren' or @text='Accept all']");
      LOG.info("cookies accepted");
    } catch (Exception ignored) {
    }
  }

  public void switchApp(String app) {
    String appPackage = "";
    switch (app.toLowerCase()) {
      case "messenger lite":
        appPackage = "com.facebook.mlite";
        break;
      case "whatsapp":
        appPackage = "com.whatsapp";
        break;
      case "chrome":
        appPackage = "com.android.chrome";
        break;
      case "sms":
        appPackage = "com.p1.chompsms";
        break;
      case "facebook":
        appPackage = "com.facebook.katana";
        break;
      case "twitter":
        appPackage = "com.twitter.android";
        break;
      default:
        throw new CucumberException("Invalid app name!");
    }
    driver.activateApp(appPackage);
    //switch context to WEBVIEW if Chrome, else switch to NATIVE_APP
    switchContextOfApp(app);
  }

  private void switchContextOfApp(String app) {
    String contextToSwitch = "";
    String currentContext = driver.getContext();
    assert currentContext != null;
    LOG.info("Current context: {}", currentContext);

    if (app.equalsIgnoreCase("chrome")) {
      contextToSwitch = "webview";
      waitForBothNativeAndWebViewContextAvailable(10, 5);
    } else {
      contextToSwitch = "NATIVE_APP";
    }
    switchToAnotherContext(contextToSwitch);
  }

  public void waitForBothNativeAndWebViewContextAvailable(int retryCount, int waitInterval) {
    int retry = 0;
    while (driver.getContextHandles().size() < 2) {
      LOG.info("The WEB VIEW context is not ready yet. Try again");
      waitFor(waitInterval).seconds();
      retry++;
      if (retry >= retryCount) {
        throw new CucumberException(
            "Web view context not loaded after " + waitInterval * retryCount + "seconds");
      }
    }
  }

  public void runWith(String user) {
    DriverUtil.getDeviceCapabilities(user);
    DriverUtil.initAndroidDriver();
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

  public void closeApps() {
    if (driver == null) {
      LOG.info("cant close apps");
    } else {
      switch (Hook.platform) {
        case "android":
          CommandLine openSwitcher = CommandLine.parse("adb -s " + System.getProperty("deviceUDID")
              + " shell input keyevent KEYCODE_APP_SWITCH");
          DefaultExecutor shell = new DefaultExecutor();
          try {
            shell.execute(openSwitcher);
          } catch (IOException ignored) {
          }
          waitFor(500).milliseconds();
          break;
        case "android-ssh":
          String openSwitcherSSH = "adb -s " + System.getProperty("deviceUDID")
              + " shell input keyevent KEYCODE_APP_SWITCH";
          adbCommand(openSwitcherSSH);
          break;
        default:
          throw new CucumberException("please choose valid android platform");
      }

      int height = driver.manage().window().getSize().height;
      int width = driver.manage().window().getSize().width;
      TouchAction action = new TouchAction(driver);
      action.press(PointOption.point(width / 2, height / 2 + 200))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
          .moveTo(PointOption.point(width / 2, height / 2)).release().perform();
      int count = 0;
      String xpath = "//android.widget.ListView[@resource-id='com.google.android.apps.nexuslauncher:id/overview_panel']//android.widget.FrameLayout";
      while (count < 120) {
        List<WebElement> openApps = driver.findElements(By.xpath(xpath));
        if (openApps.isEmpty()) {
          break;
        } else {
          action.press(PointOption.point(width / 2, height / 2))
              .waitAction(WaitOptions.waitOptions(Duration.ofMillis(100)))
              .moveTo(PointOption.point(width / 2, height / 5)).release().perform();
        }
        waitFor(500).milliseconds();
        count++;
      }
    }
  }

  public void closeAllRunningApps() {
    int attempts = 0;
    String clearAllButtonLocator = "//*[@resource-id='com.android.launcher3:id/clear_all' or @resource-id='com.google.android.apps.nexuslauncher:id/clear_all']";
    switch (Hook.platform) {
      case "android-webApp":
      case "android-nativeApp":
        CommandLine openSwitcher = CommandLine.parse("adb -s " + System.getProperty("deviceUDID")
            + " shell input keyevent KEYCODE_APP_SWITCH");
        CommandLine goLeft = CommandLine.parse("adb -s " + System.getProperty("deviceUDID")
            + " shell input keyevent KEYCODE_DPAD_LEFT");
        DefaultExecutor shell = new DefaultExecutor();
        //Open the app switcher and go left once
        try {
          shell.execute(openSwitcher);
          shell.execute(goLeft);
          waitForElementByAccessibilityId(5, 500L, "No recent items", false);
          LOG.info("No running app. Skip...");
        } catch (Exception e) {
          clearRunningApps(shell, goLeft, clearAllButtonLocator);
        }
        break;
      case "android-ssh":
        try {
          String openSwitcherSSH = "adb -s " + System.getProperty("deviceUDID")
              + " shell input keyevent KEYCODE_APP_SWITCH";
          String goLeftSSH = "adb -s " + System.getProperty("deviceUDID")
              + " shell input keyevent KEYCODE_DPAD_LEFT";
          adbCommand(openSwitcherSSH);
          adbCommand(goLeftSSH);
          adbCommand(goLeftSSH);
          adbCommand(goLeftSSH);
          while (attempts < 100) {
            adbCommand(goLeftSSH);
            if (!driver.findElements(By.xpath(clearAllButtonLocator)).isEmpty()) {
              tapElementByXpath(clearAllButtonLocator);
              break;
            }
            attempts++;
          }
        } catch (Exception ignored) {
        }
        break;
      default:
        throw new CucumberException("please choose valid android platform");
    }
  }

  public void clearRunningApps(DefaultExecutor shell, CommandLine goLeft,
      String clearAllButtonLocator) {
    int swipeCounter = 0;
    LOG.info("Found running app(s), try to clear them all");
    do {
      try {
        shell.execute(goLeft);
        swipeCounter++;
        tapElementByXpath(clearAllButtonLocator);
        LOG.info("Close successfully {} running apps", swipeCounter);
        break;
      } catch (Exception ex) {
        LOG.info("Clear all button not in view. Total swipe left: {} time(s)", swipeCounter);
      }
    } while (swipeCounter < 20);
  }

  public void waitForInvisibilityOfElement(long timeOutInSeconds, long sleepInMillis,
      String elementXpath, boolean clickable) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds),
        Duration.ofMillis(sleepInMillis));
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(elementXpath)));
  }

  public ScrollObjectBuilder scrollTo() {
    return new ScrollObjectBuilder();
  }

  @SuppressWarnings({"rawtypes", "deprecation"})
  public class ScrollObjectBuilder {

    private String elementName;
    private String elementXpath;
    private PointOption endPoint;

    public ScrollObjectBuilder element() {
      return this;
    }

    public ScrollObjectBuilder element(String elementName, String elementXpath) {
      this.elementName = elementName;
      this.elementXpath = elementXpath;
      return this;
    }

    public void containsText(String text) {
      LOG.info("Try to scroll to expected element having text {}", text);
      driver.findElement(AppiumBy.androidUIAutomator(
          "new UiScrollable(new UiSelector().scrollable(true))" +
              ".scrollTextIntoView(\"" + text + "\")"));
    }

    public void inWebView() {
      int attempts = 0;
      do {
        try {
          waitForElementByXpath(2, 500L, elementXpath, false);
          LOG.info("Found expected element \"{}\"", elementName);
          break;
        } catch (Exception e) {
          attempts++;
          if (attempts == 1) {
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).flingToEnd(10);"));
            LOG.info("Expected element {} not visible. Scroll to bottom", elementName);
          } else {
            LOG.info("Expected element {} not visible. Continue scrolling up and find",
                elementName);
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollBackward(80);"));
          }
        }
      } while (attempts < 60);
      if (attempts >= 60) {
        throw new CucumberException(
            "Expected element " + elementName + " not visible in current page");
      }
    }

    public ScrollObjectBuilder point(PointOption endPoint) {
      this.endPoint = endPoint;
      return this;
    }

    public void fromPoint(PointOption startPoint) {
      TouchAction action = new TouchAction(driver);
      action.longPress(startPoint).moveTo(endPoint).release().perform();
    }
  }

  //Verify status of an element whether it is checked or not via attribute
  public void verifyElementCheckedStatusViaAttribute(String elementName, String elementXpath,
      String status, String attribute) {
    scrollTo().element(elementName, elementXpath).inWebView();
    int retry = 0;
    do {
      try {
        String receiverTypeActualStatus = waitForElementByXpath(30, 1000L, elementXpath,
            false).getAttribute(attribute);
        if (status.equals("checked") || status.equals("ticked")) {
          Assert.assertEquals("true", receiverTypeActualStatus);
        } else {
          Assert.assertEquals("false", receiverTypeActualStatus);
        }
        LOG.info("Element {} has status {} as expectation", elementName, status);
        break;
      } catch (AssertionError assertionError) {
        LOG.info("Element status seems not to be updated successfully. Try again in 5 seconds");
        retry++;
        waitFor(5).seconds();
      }
    } while (retry < 60);
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

  //Pick a specific date in calendar
  public void pickDateFromCalendar(String month, String day, String year) {
    //Select desired year
    selectYearInCalendar(year);
    //Select desired month
    selectMonthInCalendar(month);
    //Select day and save
    selectDay(day);
    tapElementByXpath(DatePickerLocators.createLibraryButton().get("set"));
    waitForInvisibilityOfElement(30, 1000L,
        DatePickerLocators.createLibraryElement().get("date picker"), false);
  }

  //Select desired day
  public void selectDay(String day) {
    String dayXpath = "//android.view.View[@text='" + day + "']";
    tapElementByXpath(dayXpath);
    verifyElementCheckedStatusViaAttribute(day, dayXpath, "checked", "checked");
  }

  //Select desired month
  public void selectMonthInCalendar(String month) {
    waitForElementByXpath(30, 1000L, DatePickerLocators.createLibraryElement().get("day picker"),
        false);
    String localDate = LocalDate.now().toString();
    String monthInView = localDate.split("-")[1];
    //Calculate month differences to know number of clicking next or previous month
    int monthDifferences = Integer.parseInt(month) - Integer.parseInt(monthInView);
    String navigateMonthButtonXpath = "";
    if (monthDifferences < 0) {
      navigateMonthButtonXpath = DatePickerLocators.createLibraryButton().get("previous month");
    } else if (monthDifferences == 0) {
      LOG.info("Do not need to change month");
    } else {
      navigateMonthButtonXpath = DatePickerLocators.createLibraryButton().get("next month");
    }
    monthDifferences = Math.abs(monthDifferences);
    //Do a loop to move to desired month
    int moveNumber = 0;
    while (moveNumber < monthDifferences) {
      tapElementByXpath(navigateMonthButtonXpath);
      moveNumber++;
    }
  }

  //Select desired year
  public void selectYearInCalendar(String year) {
    waitForElementByXpath(30, 1000L, DatePickerLocators.createLibraryElement().get("date picker"),
        false);
    tapElementByXpath(DatePickerLocators.createLibraryButton().get("header year"));
    waitForElementByXpath(30, 1000L, DatePickerLocators.createLibraryElement().get("year picker"),
        false);
    String yearXpath = "//android.widget.TextView[@text='" + year + "']";
    scrollTo().element(year, yearXpath).inWebView();
    tapElementByXpath(yearXpath);
  }

  @SuppressWarnings("rawtypes")
  public void selectValueInsideDropdown(String value, String dropdownXpath,
      String firstElementInsideXpath, String lastElementInsideXpath) {
    //Calculate {startPoint, endPoint} to scroll inside dropdown
    WebElement openedDropdown = waitForElementByXpath(30, 500L, dropdownXpath, false);
    int dropdownHeight = openedDropdown.getSize().getHeight();
    WebElement firstElementInDropdown = waitForElementByXpath(10, 500L, firstElementInsideXpath,
        false);
    int elementHeightInDropdown = firstElementInDropdown.getSize().getHeight();
    int totalElementDisplaySameTime = dropdownHeight / elementHeightInDropdown;
    Point firstElementCoordinate = firstElementInDropdown.getLocation();
    //Set startPoint
    int startPointX =
        openedDropdown.getLocation().getX() + firstElementInDropdown.getSize().getWidth() + 2;
    int startPointY =
        firstElementCoordinate.getY() + elementHeightInDropdown * totalElementDisplaySameTime;
    PointOption startPoint = PointOption.point(startPointX, startPointY);
    //Set endPoint
    PointOption endPoint = PointOption.point(startPointX,
        firstElementInDropdown.getLocation().getY());
    //Scroll until value found
    boolean isAtBottomList = false;
    do {
      try {
        waitForElementByXpath(2, 500L, "//*[@text='" + value + "']", true);
        LOG.info("Found value \"{}\" option", value);
        tapElementByXpath("//*[@text='" + value + "']");
        waitForInvisibilityOfElement(5, 500L, dropdownXpath, false);
        LOG.info("Select value \"{}\" successfully", value);
        break;
      } catch (TimeoutException e) {
        if (isAtBottomList) {
          LOG.info("Value {} is not exist in list", value);
          throw new CucumberException("Expected value not in list");
        } else {
          LOG.info("Value \"{}\" is not visible. Try to scroll to find...", value);
        }
        scrollTo().point(endPoint).fromPoint(startPoint);
        try {
          waitForElementByXpath(1, 200L, lastElementInsideXpath, true);
          isAtBottomList = true;
        } catch (TimeoutException ignored) {
          LOG.info("Dropdown list not reach to bottom");
        }
      }
    } while (true);
  }

  public void scrollBackward(int steps) {
    driver.findElement(AppiumBy.androidUIAutomator(
        "new UiScrollable(new UiSelector().scrollable(true)).scrollBackward(" + steps + ");")
    );
    LOG.info("Scroll backward with step {}", steps);
  }

  //Enter text to field in web view context
  public void enterValueToTextFieldInWebView(String value, String textField,
      String textFieldXpath) {
    value = TestDataLoader.getTestData(value);
    scrollTo().element(textField, textFieldXpath).inWebView();
    if (value.equals("empty")) {
      LOG.info("Text field {} was left empty", textField);
    } else {
      typeTextIntoElementByXpath(textFieldXpath, value);
    }
    LOG.info("Text {} was inputted to field {}", value, textField);
  }

  //Click an object in web view context
  public void clickObjectInWebView(String object, String objectXpath) {
    scrollTo().element(object, objectXpath).inWebView();
    tapElementByXpath(objectXpath);
    LOG.info("Tapped to element {}", object);
  }

  public void switchToAnotherContext(String otherContext) {
    Set<String> contexts = driver.getContextHandles();
    otherContext = otherContext.toLowerCase();
    if (!Objects.requireNonNull(driver.getContext()).toLowerCase().contains(otherContext)) {
      for (String context : contexts) {
        if (context.toLowerCase().contains(otherContext)) {
          driver.context(context);
          LOG.info("Switched context to: {}", context);
          break;
        }
      }
    }
  }

  public void declineChromeNotificationPopup() {
    try {
      String chromeNotificationDeclineButtonXpath = "//*[@resource-id='com.android.chrome:id/negative_button']";
      waitForElementByXpath(5, 500, chromeNotificationDeclineButtonXpath, false);
      tapElementByXpath(chromeNotificationDeclineButtonXpath);
    } catch (Exception e) {
      LOG.info("Chrome notification popup not appear!");
    }
  }
}
