package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.core.exception.CucumberException;
import java.io.IOException;
import java.util.Map;
import locators.android.AndroidChromeLocators;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import steps.Hook;

public class AndroidChromePage extends AndroidBasicPage {

  private static final Logger LOG = LogManager.getLogger(AndroidChromePage.class);
  private static final String MAC_IP_ADDRESS = "192.168.178.39";
  private final Map<String, String> xpathToLabel = AndroidChromeLocators.createLabelLibrary();
  private final Map<String, String> xpathToButton = AndroidChromeLocators.createLabelLibraryButton();
  private final Map<String, String> xpathToTextfield = AndroidChromeLocators.createLabelLibraryTextfield();
  private final Map<String, String> xpathToUrl = AndroidChromeLocators.createLabelLibraryUrl();
  private final Map<String, String> xpathToTitle = AndroidChromeLocators.createLabelLibraryTitle();
  private final Map<String, String> xpathToLandingPage = AndroidChromeLocators.createLabelLibraryLandingPage();


  public AndroidChromePage(AndroidDriver driver) {
    super(driver);
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

  public void removeChromeData() {
    //Initialization
    CommandLine cmdClear = CommandLine.parse(
      "adb -s " + System.getProperty("deviceUDID") + " shell pm clear com.android.chrome");
//        CommandLine cmdStart = CommandLine.parse("adb -s " + System.getProperty("deviceUDID") + " shell am start -n com.android.chrome/com.google.android.apps.chrome.Main");
    CommandLine cmdStop = CommandLine.parse(
      "adb -s " + System.getProperty("deviceUDID") + " shell am force-stop com.android.chrome");

    DefaultExecutor shell = new DefaultExecutor();
    int exitValue = 9999;
    //Delete app cache and data
    try {
      exitValue = shell.execute(cmdClear);
    } catch (IOException ignored) {
    }
    if (exitValue > 0) {
      throw new CucumberException("Cannot remove Chrome app data!");
    }
    //Start app
//        try {
//            exitValue = shell.execute(cmdStart);
//        } catch (IOException ignored) {
//        }
//        if (exitValue > 0) throw new CucumberException("Cannot start Chrome app!");
    //Accept terms and conditions
//        tapElementById("com.android.chrome:id/send_report_checkbox");
//        tapElementById("com.android.chrome:id/terms_accept");
//        //Decline the sync option (if prompted)
//        try {
//            waitForElementById(3, 500, "com.android.chrome:id/negative_button", false).click();
//        } catch (TimeoutException ignored) {
//        }
//        //Disable password saving and auto-translate suggestions
//        tapElementByAccessibilityId("More options");
//        tapElementByAccessibilityId("Settings");
//        tapElementByXpath("//android.widget.TextView[@text='Passwords']");
//        tapElementById("com.android.chrome:id/switchWidget");
//        tapElementByAccessibilityId("Navigate up");
//        waitForElementByXpath(30, 2000, xpathToLabel.get("settingsMenuFirstElement"), false);
//        driver.findElement(MobileBy.AndroidUIAutomator(("new UiScrollable(new UiSelector().resourceId(\"com.android.chrome:id/recycler_view\")).scrollIntoView(new UiSelector().text(\"Languages\"));"))).click();
//        tapElementById("com.android.chrome:id/switchWidget");
//        tapElementByAccessibilityId("Navigate up");
//        tapElementByAccessibilityId("Navigate up");
//        //Re-start app to get rid of the Lite Mode popup
    try {
      exitValue = shell.execute(cmdStop);
    } catch (IOException ignored) {
    }
    if (exitValue > 0) {
      throw new CucumberException("Cannot stop Chrome app!");
    }
//        try {
//            exitValue = shell.execute(cmdStart);
//        } catch (IOException ignored) {
//        }
//        if (exitValue > 0) throw new CucumberException("Cannot start Chrome app!");
//        try {
//            waitForElementById(3, 500, "com.android.chrome:id/button_secondary", false).click();
//        } catch (TimeoutException ignored) {
//        }
//        //Close all tabs
//        tapElementById("com.android.chrome:id/tab_switcher_button");
//        tapElementByAccessibilityId("More options");
//        tapElementByAccessibilityId("Close all tabs");
  }

  public void removeChromeDataSSH() {
    String cmdClear =
      "adb -s " + System.getProperty("deviceUDID") + " shell pm clear com.android.chrome";
    String cmdStart = "adb -s " + System.getProperty("deviceUDID")
      + " shell am start -n com.android.chrome/com.google.android.apps.chrome.Main";
    String cmdStop =
      "adb -s " + System.getProperty("deviceUDID") + " shell am force-stop com.android.chrome";

    if (adbCommand(cmdClear) > 0) {
      LOG.info("Cannot remove Chrome app data!");
    }
    if (adbCommand(cmdStart) > 0) {
      LOG.info("Cannot start Chrome app!");
    }
    //Accept terms and conditions
    tapElementById("com.android.chrome:id/send_report_checkbox");
    tapElementById("com.android.chrome:id/terms_accept");
    try {
      //Decline the sync option (if prompted)
      waitForElementById(3, 500, "com.android.chrome:id/negative_button", false).click();
      waitForElementByXpath(1, 500,
        "//*[@resource-id='com.android.chrome:id/promo_dialog_layout']//*[@resource-id='com.android.chrome:id/button_bar']//android.widget.Button[contains(@text,'No')]",
        true).click();
      //Disable password saving and auto-translate suggestions
      tapElementByAccessibilityId("More options");
      tapElementByAccessibilityId("Settings");
      tapElementByXpath("//android.widget.TextView[@text='Passwords']");
      tapElementById("com.android.chrome:id/switchWidget");
      tapElementByAccessibilityId("Navigate up");
      waitForElementByXpath(30, 2000, xpathToLabel.get("settingsMenuFirstElement"), false);
      driver.findElement(AppiumBy.androidUIAutomator(
          ("new UiScrollable(new UiSelector().resourceId(\"com.android.chrome:id/recycler_view\")).scrollIntoView(new UiSelector().text(\"Languages\"));")))
        .click();
      tapElementById("com.android.chrome:id/switchWidget");
      tapElementByAccessibilityId("Navigate up");
      tapElementByAccessibilityId("Navigate up");
      if (adbCommand(cmdStop) > 0) {
        LOG.info("Cannot stop Chrome app!");
      }
      if (adbCommand(cmdStart) > 0) {
        waitForElementById(3, 500, "com.android.chrome:id/button_secondary", false).click();
      }
      waitForElementByXpath(2, 500,
        "//*[@resource-id='com.android.chrome:id/promo_dialog_layout']//*[@resource-id='com.android.chrome:id/button_bar']//android.widget.Button[contains(@text,'No')]",
        true).click();
      //Close all tabs
      tapElementById("com.android.chrome:id/tab_switcher_button");
      tapElementByAccessibilityId("More options");
      tapElementByAccessibilityId("Close all tabs");
      waitForElementByXpath(2, 500,
        "//*[@resource-id='com.android.chrome:id/promo_dialog_layout']//*[@resource-id='com.android.chrome:id/button_bar']//android.widget.Button[contains(@text,'No')]",
        true).click();
    } catch (TimeoutException ignored) {
    }
  }

  public void verifyLandingPageUrlTitle(String page, String url, String title) {
    try {
      driver.hideKeyboard();
    } catch (Exception ignored) {
    }
    cookiesPopUpChromeApp(5);
    String urlShown = waitForElementById(30, 1000, "com.android.chrome:id/url_bar", true).getText();
    LOG.info("url in chrome app: \"{}\" vs. expected \"{}\"", urlShown, xpathToUrl.get(url));
    String titleShown = waitForElementById(30, 500, "com.android.chrome:id/compositor_view_holder",
      true).findElement(By.xpath("//android.webkit.WebView")).getText();
    LOG.info("title shown in chrome app: {} vs. expected \"{}\"", titleShown,
      xpathToTitle.get(title));
    waitForElementByXpath(30, 500, xpathToLandingPage.get(page), false);
    Assert.assertTrue(titleShown.contains(xpathToTitle.get(title)));
    Assert.assertTrue(urlShown.contains(xpathToUrl.get(url)));
  }

  public void enterText(String value, String textfield, String page) {
    try {
      driver.hideKeyboard();
    } catch (Exception ignored) {
    }
    if (textfield.equalsIgnoreCase("cdp login pin")) {
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp login pin1"), value.substring(0, 1));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp login pin2"), value.substring(1, 2));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp login pin3"), value.substring(2, 3));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp login pin4"), value.substring(3, 4));
    } else if (textfield.equalsIgnoreCase("cdp old pin")) {
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp old pin1"), value.substring(0, 1));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp old pin2"), value.substring(1, 2));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp old pin3"), value.substring(2, 3));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdp old pin4"), value.substring(3, 4));
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
    } else if (textfield.contains("cdpLoginPagePinTextbox")) {
      typeTextIntoElementByXpath(xpathToTextfield.get("cdpLoginPinDigit0"), value.substring(0, 1));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdpLoginPinDigit1"), value.substring(1, 2));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdpLoginPinDigit2"), value.substring(2, 3));
      typeTextIntoElementByXpath(xpathToTextfield.get("cdpLoginPinDigit3"), value.substring(3, 4));
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
    try {
      driver.hideKeyboard();
    } catch (Exception ignored) {
    }
    waitForElementByXpath(300, 250, xpathToButton.get(button), false).click();
    LOG.info("clicks button {} on {} page", button, page);
    if ("cdp login".equalsIgnoreCase(button)) {
      waitFor(3).seconds();
      if (!driver.findElements(By.xpath(("//android.widget.TextView[contains(@text,'Fehler')]")))
        .isEmpty()) {
        tapElementByXpath(xpathToButton.get(button));
      }
    }
    if (button.equalsIgnoreCase("cdp login") && page.equalsIgnoreCase("cdp main")) {
      tapElementByXpath("//android.widget.Button[contains(@text,'Login')]");
    }
  }

  public void closeAllTab() {
    try {
      driver.hideKeyboard();
    } catch (Exception ignored) {
    }
    tapElementById("com.android.chrome:id/tab_switcher_button");
    tapElementByAccessibilityId("More options");
    tapElementByAccessibilityId("Close all tabs");
  }

  public void checkText(String text) {
    try {
      driver.hideKeyboard();
    } catch (Exception ignored) {
    }
    cookiesPopUpChromeApp(1);
    waitForElementByXpath(100, 250, "//*[contains(@text,'" + text + "')]", false);
  }

  public void checkTextNotVisible(String text) {
    try {
      driver.hideKeyboard();
    } catch (Exception ignored) {
    }
    cookiesPopUpChromeApp(1);
    Assert.assertTrue(
      driver.findElements(By.xpath(("//*[contains(@text,'" + text + "')]"))).isEmpty());
  }

  public void openURLAndroidChrome(String url) {
    try {
      driver.hideKeyboard();
    } catch (Exception ignored) {
    }
    switch (Hook.platform) {
      case "android-webApp":
      case "android-nativeApp":
        LOG.info("Opening link with the default browser: {}", url);
        CommandLine cmd = CommandLine.parse("adb -s " + System.getProperty("deviceUDID")
          + " shell am start -a android.intent.action.VIEW -d " + url);
        DefaultExecutor executor = new DefaultExecutor();
        int exitValue = 9999;
        try {
          exitValue = executor.execute(cmd);
        } catch (IOException ignored) {
        }
        if (exitValue != 0) {
          throw new CucumberException("Cannot open URL via adb!");
        }
        waitFor(3).seconds();
        break;
      case "android-ssh":
        url = "'" + url + "'";
        LOG.info("Opening link with the default browser: {}", url);
        String cmdSSH = "adb -s " + System.getProperty("deviceUDID") + " shell am start -d " + url
          + " -a android.intent.action.VIEW";
        LOG.info("command to open link: {}", cmdSSH);
        if (adbCommand(cmdSSH) != 0) // throw new CucumberException("Cannot open URL via adb!");
        {
          waitFor(3).seconds();
        }
        break;
      default:
        throw new CucumberException("please choose valid android platform");
    }
    if (url.contains("skycdp")) {
      LOG.info("found cdp link");
    }
    cookiesPopUpChromeApp(5);
    waitFor(10).seconds();
  }

  public void checkButtonStatus(String button, String status) {
    try {
      driver.hideKeyboard();
    } catch (Exception ignored) {
    }
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
    try {
      driver.hideKeyboard();
    } catch (Exception ignored) {
    }
    waitFor(1).seconds();
    String locator = xpathToTextfield.get(errorType) + "[contains(@text,'" + errorText + "')]";
    WebElement error = waitForElementByXpath(30, 500, locator, false);
    String shownText = error.getText();
    LOG.info("expected: \"{}\" vs. shown: \"{}\"", errorText, shownText);
    Assert.assertTrue(shownText.contains(errorText));
    LOG.info("found expected error message on {}", page);
  }

  public void enterRestrictedSkyCdpArea(String usr, String pwd) {
    typeTextIntoElementById("com.android.chrome:id/username", usr);
    typeTextIntoElementById("com.android.chrome:id/password", pwd);
    tapElementById("android:id/button1");
  }

  public void switchToWebviewContext() {
    LOG.info("Switching to webview context on chrome...");
    driver.context("WEBVIEW_chrome");
  }

  public void changeParentalControlLevel() {
    //We don't really care which level to select, we only need to change it
    //So, start from the bottom and find the first one which is selectable
    for (int i = 5; i > 0; i--) {
      tapElementByXpath("(//div[@class='card card_true' or @class='card card_false'])[" + i + "]");
      try {
        waitForElementByXpath(3, 800, "//input[@data-test-id='default-pin-0']", false);
        break;
      } catch (TimeoutException ignored) {
        LOG.info("TimeoutException ignored");
      }
    }
  }

  public void verifyElementVisible(String element) {
    waitForElementByXpath(10, 250, xpathToLabel.get(element), false);
  }
}
